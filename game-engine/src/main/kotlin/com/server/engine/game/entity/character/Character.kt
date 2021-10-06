package com.server.engine.game.entity.character

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentManager
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class Character : ComponentManager<Component> {

    private val _components = mutableMapOf<KClass<*>, Component>()
    val components: Map<KClass<*>, Component> get() = _components

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

    inline operator fun <reified C : Component> getValue(ref: Any?, prop: KProperty<*>) : C {
        return component()
    }

    inline fun <reified C : Component> component() : C = components[C::class] as C

    companion object {

        inline fun <reified C : Component> Character.with(comp: C) = apply {
            addComponent(comp)
        }
        inline fun <reified C: Component> Character.has() : Boolean = hasComponent(C::class)
    }
}