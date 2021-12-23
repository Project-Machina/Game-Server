package com.server.engine.game.components.managers

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine.Companion.with
import com.server.engine.utilities.get
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named
import java.util.*
import kotlin.reflect.KClass

open class SimpleComponentManager : ComponentManager<Component> {

    private val _components = mutableMapOf<KClass<out Component>, Component>()
    val components: Map<KClass<out Component>, Component> = _components

    override fun addComponent(component: Component): ComponentManager<Component> {
        _components.putIfAbsent(component::class, component)
        return this
    }

    override fun removeComponent(kclass: KClass<out Component>): ComponentManager<Component> {
        _components.remove(kclass)
        return this
    }

    override fun hasComponent(kclass: KClass<out Component>): Boolean {
        return _components.containsKey(kclass)
    }

    override fun putComponent(component: Component): ComponentManager<Component> {
        _components[component::class] = component
        return this
    }

    override fun addSingletonComponent(
        kclass: KClass<out Component>,
        component: Component
    ): ComponentManager<Component> {
        _components[kclass] = component
        return this
    }

    override fun saveComponents(): JsonObject {
        return buildJsonObject {
            putJsonArray("components") {
                components.values.forEach {
                    add(buildJsonObject {
                        put("compType", it::class.simpleName)
                        put("attributes", it.save())
                    })
                }
            }
        }
    }

    override fun loadComponents(json: JsonObject) {
        if (json.containsKey("components")) {
            val comps = json["components"]?.jsonArray ?: JsonArray(emptyList())
            for (comp in comps) {
                val obj = comp.jsonObject
                if (obj.containsKey("compType") && obj.containsKey("attributes")) {
                    val type = obj["compType"]!!.jsonPrimitive.content
                    val compFactory: ComponentFactory<out Component> = get(named(type))
                    val c = compFactory.create()
                    c.load(obj["attributes"]!!.jsonObject)
                    with(c)
                }
            }
        }
    }

    companion object {
        inline fun <reified C : Component> SimpleComponentManager.component() = components[C::class] as C
        inline fun <reified C : Component> SimpleComponentManager.has() = hasComponent(C::class)
        inline fun <reified C: Component> SimpleComponentManager.with(comp: C, init: C.() -> Unit = {}) = apply {
            init(comp)
            addComponent(comp)
        }
    }
}