package com.server.engine.game.vms.components

import com.server.engine.game.vms.UpgradableComponent
import com.server.engine.game.vms.VMComponent
import com.server.engine.game.vms.upgrades.NetworkCardUpgradeComponent
import kotlin.math.roundToInt

class NetworkCardComponent(override val upgrades: UpgradableComponent = NetworkCardUpgradeComponent()) : VMComponent {


}