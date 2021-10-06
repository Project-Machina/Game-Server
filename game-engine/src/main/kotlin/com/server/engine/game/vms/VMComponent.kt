package com.server.engine.game.vms

import com.server.engine.game.components.Component

interface VMComponent : Component {

    val upgrades: UpgradableComponent

}