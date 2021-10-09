package com.server.engine.game.vms

import com.server.engine.game.components.Component
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface UpgradableComponent : Component {

    val level: Double

    fun nextUpgradeCost(level: Double) : Int

    fun upgradeBy(levels: Double) : Int
    fun downgradeBy(levels: Double)

    fun reset()

    override fun save(): JsonObject {
        return buildJsonObject {
            put("level", level)
        }
    }

    companion object : UpgradableComponent {
        val BASE_COST = 10
        override val level: Double = 0.0

        override fun nextUpgradeCost(level: Double): Int {
            return 0
        }

        override fun upgradeBy(levels: Double): Int {
            return 0
        }

        override fun downgradeBy(levels: Double) {}

        override fun reset() {}
    }
}