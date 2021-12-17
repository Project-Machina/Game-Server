package com.server.engine.game.entity.vms.components.power

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentFactory
import kotlinx.serialization.json.*

class PoweredComponent(watts: Int) : Component {

    var watts: Int = watts
        private set

    override fun save(): JsonObject {
        return buildJsonObject {
            put("required_watts", watts)
        }
    }

    override fun load(json: JsonObject) {
        watts = json["required_watts"]?.jsonPrimitive?.int ?: 0
    }

    companion object : ComponentFactory<PoweredComponent> {
        val NO_POWER = PoweredComponent(0)
        override fun create(): PoweredComponent {
            return PoweredComponent(0)
        }
    }
}