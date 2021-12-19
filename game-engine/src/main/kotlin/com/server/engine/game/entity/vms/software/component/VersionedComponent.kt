package com.server.engine.game.entity.vms.software.component

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.software.SoftwareComponent
import kotlinx.serialization.json.*
import kotlin.math.pow

class VersionedComponent : SoftwareComponent {

    var version: Double = 1.0

    override val id: String get() = "$version"

    override val size: Long
        get() = (version.pow(3) / 3).toLong()

    override fun save(): JsonObject {
        return buildJsonObject {
            put("version", version)
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("version")) {
            version = json["version"]!!.jsonPrimitive.double
        }
    }

    companion object : ComponentFactory<VersionedComponent> {
        override fun create(): VersionedComponent {
            return VersionedComponent()
        }
    }
}