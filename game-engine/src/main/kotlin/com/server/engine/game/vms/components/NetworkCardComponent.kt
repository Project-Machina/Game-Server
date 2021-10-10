package com.server.engine.game.vms.components

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.vms.UpgradableComponent
import com.server.engine.game.vms.VMComponent
import com.server.engine.game.vms.upgrades.NetworkCardUpgradeComponent

class NetworkCardComponent(override val upgrades: UpgradableComponent = NetworkCardUpgradeComponent()) : VMComponent {



    companion object : ComponentFactory<NetworkCardComponent> {
        override fun create(): NetworkCardComponent {
            return NetworkCardComponent()
        }
    }
}