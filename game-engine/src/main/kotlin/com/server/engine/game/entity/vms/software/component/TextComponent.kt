package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import com.server.engine.game.entity.vms.VirtualMachine
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class TextComponent : SoftwareComponent {
    var text: String = ""

    override val id: String get() = "${text.hashCode()}"

    override suspend fun run(source: com.server.engine.game.entity.vms.VirtualMachine, target: com.server.engine.game.entity.vms.VirtualMachine): Boolean {
        return true
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