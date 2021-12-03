package com.server.engine.game.vms

import com.server.engine.game.components.Component
import com.server.engine.game.vms.components.power.PoweredComponent

interface VMComponent : Component {

    val upgrades: UpgradableComponent
        get() = UpgradableComponent
    val powerConsumption: PoweredComponent
        get() = PoweredComponent.NO_POWER

}