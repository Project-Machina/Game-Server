package com.server.engine.game.entity.vms.processes

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.utilities.boolean
import com.server.engine.utilities.get
import com.server.engine.utilities.string
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

class VirtualProcess(
    var name: String,
    var immediate: Boolean = false,
    var isIndeterminate: Boolean = false
) : ComponentManager<ProcessComponent> {

    private val _components = mutableMapOf<KClass<out ProcessComponent>, ProcessComponent>()
    val components: Map<KClass<out ProcessComponent>, ProcessComponent> = _components

    val minimalRunningTime: Long get() = _components.values.sumOf { it.runningTime }
    val threadCost: Int get() {
        return if(isPaused || isComplete) 0 else _components.values.sumOf { it.threadCost }
    }
    val ramCost: Long get() {
        val total = _components.values.sumOf { it.ramCost }
        if(isIndeterminate)
            return total
        return if(isPaused || isComplete) (total / 2) else total
    }
    val networkCost: Int get() {
        return if(isPaused || isComplete) 0 else _components.values.sumOf { it.networkCost }
    }

    val isComplete: Boolean get() {
        val runTime = if(preferredRunningTime <= minimalRunningTime) {
            minimalRunningTime
        } else {
            preferredRunningTime
        }
        return elapsedTime >= runTime
    }
    var preferredRunningTime: Long = minimalRunningTime
    var elapsedTime: Long = 0
    var pid: Int = -1
    var isPaused: Boolean = false
    var shouldComplete: Boolean = false
    var isKilled: Boolean = false

    override fun saveComponents(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("immediate", immediate)
            put("paused", isPaused)
            put("isIndeterminate", isIndeterminate)
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
        if(json.containsKey("name")) {
            name = json.string("name")
        }
        if(json.containsKey("immediate")) {
            immediate = json.boolean("immediate")
        }
        if(json.containsKey("paused")) {
            isPaused = json.boolean("paused")
        }
        if(json.containsKey("isIndeterminate")) {
            isIndeterminate = json.boolean("isIndeterminate")
        }
        if (json.containsKey("components")) {
            val comps = json["components"]?.jsonArray ?: JsonArray(emptyList())
            for (comp in comps) {
                val obj = comp.jsonObject
                if (obj.containsKey("compType") && obj.containsKey("attributes")) {
                    val type = obj["compType"]!!.jsonPrimitive.content
                    val compFactory: ComponentFactory<out ProcessComponent> = get(named(type))
                    val c = compFactory.create()
                    c.load(obj["attributes"]!!.jsonObject)
                    if(c is OnFinishProcessComponent) {
                        singleton<OnFinishProcessComponent>(c)
                    } else {
                        with(c)
                    }
                }
            }
        }
    }

    override fun addComponent(component: ProcessComponent): ComponentManager<ProcessComponent> = apply {
        _components.putIfAbsent(component::class, component)
    }

    override fun putComponent(component: ProcessComponent) = apply {
        _components[component::class] = component
    }

    override fun removeComponent(kclass: KClass<out ProcessComponent>): ComponentManager<ProcessComponent> = apply {
        _components.remove(kclass)
    }

    override fun hasComponent(kclass: KClass<out ProcessComponent>): Boolean {
        return _components.containsKey(kclass)
    }

    override fun addSingletonComponent(
        kclass: KClass<out ProcessComponent>,
        component: ProcessComponent
    ): ComponentManager<ProcessComponent> {
        _components[kclass] = component
        return this
    }

    companion object {
        val NO_PROCESS = VirtualProcess("no_process")
        inline fun <reified C : ProcessComponent> VirtualProcess.with(comp: C) {
            addComponent(comp)
        }
        inline fun <reified C : ProcessComponent> VirtualProcess.replace(comp: C) {
            putComponent(comp)
        }
        inline fun <reified C : ProcessComponent> VirtualProcess.component() : C {
            return components[C::class] as C
        }

        inline fun <reified C : ProcessComponent> VirtualProcess.has() : Boolean {
            return hasComponent(C::class)
        }

        inline fun <reified BASE : ProcessComponent> VirtualProcess.singleton(comp: ProcessComponent) {
            addSingletonComponent(BASE::class, comp)
        }
    }
}