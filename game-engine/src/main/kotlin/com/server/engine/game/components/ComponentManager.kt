package com.server.engine.game.components

import com.server.engine.game.components.dsl.ComponentDSL
import kotlin.reflect.KClass

interface ComponentManager<BASE : Component> {

    fun addComponent(component: BASE): ComponentManager<BASE>
    fun removeComponent(kclass: KClass<out BASE>) : ComponentManager<BASE>
    fun hasComponent(kclass: KClass<out BASE>) : Boolean

    companion object {
        inline fun <reified BASE : Component> ComponentManager<BASE>.with(block: ComponentDSL<BASE>.() -> Unit) {
            ComponentDSL(this).block()
        }
    }
}