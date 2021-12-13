package com.server.engine.game.vms

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.vms.components.connection.ConnectionComponent
import com.server.engine.utilities.get
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

class VirtualMachine private constructor() : ComponentManager<VMComponent> {

    private val _components = mutableMapOf<KClass<*>, VMComponent>()
    val components: Map<KClass<*>, VMComponent> get() = _components

    fun init() {
        with(ConnectionComponent())
    }

    override fun addComponent(component: VMComponent): ComponentManager<VMComponent> {
        _components.putIfAbsent(component::class, component)
        return this
    }

    override fun removeComponent(kclass: KClass<out VMComponent>): ComponentManager<VMComponent> {
        _components.remove(kclass)
        return this
    }

    override fun hasComponent(kclass: KClass<out VMComponent>): Boolean {
        return _components.containsKey(kclass)
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
                    val compFactory: ComponentFactory<out VMComponent> = get(named(type))
                    val c = compFactory.create()
                    c.load(obj["attributes"]!!.jsonObject)
                    with(c)
                }
            }
        }
    }

    companion object {
        inline fun <reified C : VMComponent> VirtualMachine.with(comp: C) = apply {
            addComponent(comp)
        }

        inline fun <reified C : VMComponent> VirtualMachine.has(): Boolean = hasComponent(C::class)
        inline fun <reified C : VMComponent> VirtualMachine.component(): C {
            return components[C::class] as C
        }

        fun create() : VirtualMachine {
            val vm = VirtualMachine()
            vm.init()
            return vm
        }

        @Deprecated(level = DeprecationLevel.WARNING, message = "This should only be use for unit tests.")
        fun unsafeCreate() : VirtualMachine {
            return VirtualMachine()
        }
    }
}