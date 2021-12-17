package com.server.engine.game.entity.vms.components.power

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import kotlinx.serialization.json.*

class PowerStorageComponent : com.server.engine.game.entity.vms.VMComponent {

    var wattsUsage: Int = 0

    fun calculatePowerTax() : Int {
        return wattsUsage * 2
    }

    fun addWatts(watts: Int) {
        wattsUsage += watts
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("watts", wattsUsage)
        }
    }

    override fun load(json: JsonObject) {
        wattsUsage = json["watts"]?.jsonPrimitive?.int ?: 0
    }

    companion object : ComponentFactory<PowerStorageComponent> {
        override fun create(): PowerStorageComponent {
            return PowerStorageComponent()
        }
    }
}