package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import com.server.engine.game.entity.vms.VirtualMachine
import kotlinx.serialization.json.*

class TextComponent : SoftwareComponent {
    var text: String = ""

    override val copy: Boolean = true

    override val id: String get() = text

    override val size: Long
        get() = ((text.length / 4) + 1).toLong()

    override fun copy(): SoftwareComponent {
        return TextComponent().also { it.text = text }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("text", text)
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("text")) {
            text = json["text"]?.jsonPrimitive?.content ?: ""
        }
    }

    companion object : ComponentFactory<TextComponent> {
        override fun create(): TextComponent = TextComponent()
    }
}