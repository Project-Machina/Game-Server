package com.server.engine.game.software

import com.server.engine.game.components.ComponentManager
import kotlin.reflect.KClass

class VirtualSoftware(val name: String, val extension: String) : ComponentManager<SoftwareComponent> {

    private val _components = mutableMapOf<KClass<*>, SoftwareComponent>()
    val components: Map<KClass<*>, SoftwareComponent> = _components

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
        inline fun <reified C: SoftwareComponent> VirtualSoftware.has() : Boolean = hasComponent(C::class)
    }
}