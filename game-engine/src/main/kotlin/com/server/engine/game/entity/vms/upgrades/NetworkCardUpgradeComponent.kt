package com.server.engine.game.entity.vms.upgrades

import com.server.engine.game.entity.vms.UpgradableComponent
import com.server.engine.game.entity.vms.UpgradableComponent.Companion.BASE_COST
import kotlin.math.roundToInt

class NetworkCardUpgradeComponent : UpgradableComponent {

    override var level: Double = 1.0

    override fun nextUpgradeCost(level: Double): Int {
        return BASE_COST + (level * 2).roundToInt()
    }

    override fun upgradeBy(levels: Double): Int {
        val current = this.level
        this.level += levels
        return nextUpgradeCost((current + levels))
    }

    override fun downgradeBy(levels: Double) {
        if(levels.isNaN() || (this.level - levels) < 0.0) {
            reset()
        } else {
            this.level -= levels
        }
    }

    override fun reset() {
        level = 1.0
    }

}