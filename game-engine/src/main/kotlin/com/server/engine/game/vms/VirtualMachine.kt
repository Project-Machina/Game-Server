package com.server.engine.game.vms

import com.server.engine.game.components.ComponentManager
import kotlin.reflect.KClass

class VirtualMachine : ComponentManager<VMComponent> {

    private val _components = mutableMapOf<KClass<*>, VMComponent>()
    val components: Map<KClass<*>, VMComponent> get() = _components

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

    companion object {
        inline fun <reified C : VMComponent> VirtualMachine.with(comp: C) = apply {
            addComponent(comp)
        }
        inline fun <reified C: VMComponent> VirtualMachine.has() : Boolean = hasComponent(C::class)
    }
}