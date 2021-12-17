package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import com.server.engine.game.entity.vms.VirtualMachine
import kotlinx.serialization.json.*

class VersionedComponent : SoftwareComponent {

    var version: Double = 1.0

    override val id: String = "$version"

    override suspend fun run(source: VirtualMachine, target: VirtualMachine): Boolean {
        return true
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("version", version)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("version")) {
            version = json["version"]!!.jsonPrimitive.double
        }
    }

    companion object : ComponentFactory<VersionedComponent> {
        override fun create(): VersionedComponent {
            return VersionedComponent()
        }
    }
}