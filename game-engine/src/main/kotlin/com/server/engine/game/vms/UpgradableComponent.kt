package com.server.engine.game.vms

import com.server.engine.game.components.Component

interface UpgradableComponent : Component {

    val level: Double

    fun nextUpgradeCost(level: Double) : Int

    fun upgradeBy(levels: Double) : Int
    fun downgradeBy(levels: Double)

    fun reset()

    companion object {
        val BASE_COST = 10
    }
}