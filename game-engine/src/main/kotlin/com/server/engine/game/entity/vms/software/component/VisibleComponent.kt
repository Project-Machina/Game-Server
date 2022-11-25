package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import com.server.engine.utilities.double
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class VisibleComponent(var hiddenVersion: Double = 0.0) : SoftwareComponent {

    override val id: String
        get() = "null"

    override fun save(): JsonObject {
        return buildJsonObject {
            put("hiddenVersion", hiddenVersion)
        }
    }

    override val copy: Boolean = true

    override fun copy(): SoftwareComponent {
        return VisibleComponent(hiddenVersion)
    }

    override fun load(json: JsonObject) {
        hiddenVersion = json.double("hiddenVersion")
    }

    companion object : ComponentFactory<VisibleComponent>{
        override fun create(): VisibleComponent {
            return VisibleComponent()
        }
    }
}