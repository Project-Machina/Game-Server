package com.server.engine.game.components.managers

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentManager
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

    companion object {
        inline fun <reified C : Component> SimpleComponentManager.component() = components[C::class] as C
        inline fun <reified C : Component> SimpleComponentManager.has() = hasComponent(C::class)
    }

    override fun addSingletonComponent(
        kclass: KClass<out Component>,
        component: Component
    ): ComponentManager<Component> {
        _components[kclass] = component
        return this
    }
}