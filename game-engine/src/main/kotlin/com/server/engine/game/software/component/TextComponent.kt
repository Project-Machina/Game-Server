package com.server.engine.game.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.software.SoftwareComponent

class TextComponent : SoftwareComponent {
    var text: String = ""
    override suspend fun execute(tick: Long): Boolean {
        return true
    }
    companion object : ComponentFactory<TextComponent> {
        override fun create() : TextComponent = TextComponent()
    }
}