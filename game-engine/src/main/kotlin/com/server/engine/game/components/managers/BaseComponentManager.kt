package com.server.engine.game.components.managers

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentManager
import kotlin.reflect.KClass

open class BaseComponentManager<BASE : Component> : ComponentManager<BASE> {
    private val _components = mutableMapOf<KClass<out Component>, Component>()
    val components: Map<KClass<out Component>, Component> = _components

    override fun addComponent(component: BASE): ComponentManager<BASE> {
        _components.putIfAbsent(component::class, component)
        return this
    }

    override fun removeComponent(kclass: KClass<out BASE>): ComponentManager<BASE> {
        _components.remove(kclass)
        return this
    }

    override fun hasComponent(kclass: KClass<out BASE>): Boolean {
        return _components.containsKey(kclass)
    }

    companion object {
        inline fun <reified BASE : Component, reified C : BASE> BaseComponentManager<BASE>.component() = components[C::class] as C
        inline fun <reified BASE : Component, reified C : BASE> BaseComponentManager<BASE>.has() = hasComponent(C::class)
    }
}