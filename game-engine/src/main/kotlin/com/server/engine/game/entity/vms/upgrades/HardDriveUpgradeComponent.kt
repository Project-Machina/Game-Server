package com.server.engine.game.entity.vms.upgrades

import com.server.engine.game.entity.vms.UpgradableComponent
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive

class HardDriveUpgradeComponent : UpgradableComponent {
    override var level: Double = 1.0
    override fun nextUpgradeCost(level: Double): Int {
        TODO("Not yet implemented")
    }

    override fun upgradeBy(levels: Double): Int {
        TODO("Not yet implemented")
    }

    override fun downgradeBy(levels: Double) {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("level")) {
            level = json["level"]?.jsonPrimitive?.double ?: 1.0
        }
    }
}