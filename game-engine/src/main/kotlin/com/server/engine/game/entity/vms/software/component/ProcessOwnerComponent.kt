package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import kotlinx.serialization.json.*

class ProcessOwnerComponent : SoftwareComponent {

    var pid: Int = -1

    override val id: String
        get() = "$pid"

    override fun save(): JsonObject {
        return buildJsonObject {
            put("pid", pid)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("pid")) {
            pid = json["pid"]!!.jsonPrimitive.int
        }
    }

    companion object : ComponentFactory<ProcessOwnerComponent> {
        override fun create(): ProcessOwnerComponent {
            return ProcessOwnerComponent()
        }
    }
}