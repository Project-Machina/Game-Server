package com.server.engine.game.entity.character

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentManager
import com.server.engine.game.entity.TickingEntity
import com.server.engine.game.world.tick.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class Character : ComponentManager<Component>, TickingEntity {

    private val _components = mutableMapOf<KClass<*>, Component>()
    val components: Map<KClass<*>, Component> get() = _components

    abstract val subscription: Subscription<out Character>?

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

    abstract override suspend fun onTick()

    abstract fun isActive() : Boolean

    inline operator fun <reified C : Component> getValue(ref: Any?, prop: KProperty<*>) : C {
        return component()
    }

    inline fun <reified C : Component> component() : C = components[C::class] as C

    companion object {

        inline fun <reified C : Component> Character.with(comp: C) = apply {
            addComponent(comp)
        }
        inline fun <reified C: Component> Character.has() : Boolean = hasComponent(C::class)

        operator fun <T> MutableStateFlow<T>.setValue(player: Character, property: KProperty<*>, t: T) {
            value = t
        }

        operator fun <T> MutableStateFlow<T>.getValue(player: Character, property: KProperty<*>): T {
            return value
        }
    }
}