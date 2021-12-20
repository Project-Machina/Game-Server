package com.server.engine.game.components

import com.server.engine.game.components.Component.Companion.BLANK_JSON_OBJECT
import com.server.engine.game.components.dsl.ComponentDSL
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

interface ComponentManager<BASE : Component> {

    fun addComponent(component: BASE): ComponentManager<BASE>
    fun putComponent(component: BASE): ComponentManager<BASE>
    fun removeComponent(kclass: KClass<out BASE>) : ComponentManager<BASE>
    fun hasComponent(kclass: KClass<out BASE>) : Boolean
    fun addSingletonComponent(kclass: KClass<out BASE>, component: BASE) : ComponentManager<BASE>

    fun saveComponents() : JsonObject {
        return BLANK_JSON_OBJECT
    }
    fun loadComponents(json: JsonObject) {}

    companion object {
        inline fun <reified BASE : Component> ComponentManager<BASE>.with(block: ComponentDSL<BASE>.() -> Unit) {
            ComponentDSL(this).block()
        }
    }
}