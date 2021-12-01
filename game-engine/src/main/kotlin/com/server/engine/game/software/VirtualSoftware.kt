package com.server.engine.game.software

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager
import com.server.engine.utilities.get
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

class VirtualSoftware(name: String, extension: String) : ComponentManager<SoftwareComponent> {

    private val _components = mutableMapOf<KClass<*>, SoftwareComponent>()
    val components: Map<KClass<*>, SoftwareComponent> = _components

    var name: String = name
        private set
    var extension: String = extension
        private set

    fun id(): String {
        val builder = StringBuilder()
        builder
            .append(name)
            .append(".")
            .append(extension)
            .append(":")
            .append(components.values.map { it.id }.joinToString(":") { it })
        return builder.toString()
    }

    override fun saveComponents(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("extension", extension)
            putJsonArray("components") {
                _components.values.forEach {
                    add(buildJsonObject {
                        put("compType", it::class.simpleName)
                        put("attributes", it.save())
                    })
                }
            }
        }
    }

    override fun loadComponents(json: JsonObject) {
        if (json.containsKey("name")) {
            name = json["name"]?.jsonPrimitive?.content ?: ""
        }
        if (json.containsKey("extension")) {
            extension = json["extension"]?.jsonPrimitive?.content ?: ""
        }
        if (json.containsKey("components") && name.isNotBlank() && name.isNotEmpty() && extension.isNotEmpty() && extension.isNotBlank()) {
            val comps = json["components"]?.jsonArray ?: JsonArray(emptyList())
            for (comp in comps) {
                val obj = comp.jsonObject
                if (obj.containsKey("compType") && obj.containsKey("attributes")) {
                    val compType = obj["compType"]?.jsonPrimitive?.content ?: ""
                    if (compType.isNotBlank() && compType.isNotEmpty()) {
                        val factory: ComponentFactory<out SoftwareComponent> = get(named(compType))
                        val component = factory.create()
                        component.load(obj["attributes"]!!.jsonObject)
                        with(component)
                    }
                }
            }
        }
    }

    override fun addComponent(component: SoftwareComponent): ComponentManager<SoftwareComponent> {
        _components.putIfAbsent(component::class, component)
        return this
    }

    override fun removeComponent(kclass: KClass<out SoftwareComponent>): ComponentManager<SoftwareComponent> {
        _components.remove(kclass)
        return this
    }

    override fun hasComponent(kclass: KClass<out SoftwareComponent>): Boolean {
        return _components.containsKey(kclass)
    }

    companion object {
        inline fun <reified C : SoftwareComponent> VirtualSoftware.with(comp: C) = apply {
            addComponent(comp)
        }

        inline fun <reified C : SoftwareComponent> VirtualSoftware.has(): Boolean = hasComponent(C::class)
        inline fun <reified C : SoftwareComponent> VirtualSoftware.component() : C {
            return components[C::class] as C
        }
    }
}