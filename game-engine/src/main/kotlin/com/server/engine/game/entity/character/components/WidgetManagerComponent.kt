package com.server.engine.game.entity.character.components

import com.server.engine.game.components.Component
import kotlinx.coroutines.flow.MutableStateFlow

class WidgetManagerComponent : Component {

    val currentWidget = MutableStateFlow("dashboard")

    fun isWidgetActive(value: String) : Boolean {
        return currentWidget.value == value
    }

}