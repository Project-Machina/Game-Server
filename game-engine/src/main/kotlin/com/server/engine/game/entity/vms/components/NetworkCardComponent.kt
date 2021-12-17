package com.server.engine.game.entity.vms.components

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.upgrades.NetworkCardUpgradeComponent

class NetworkCardComponent(override val upgrades: com.server.engine.game.entity.vms.UpgradableComponent = NetworkCardUpgradeComponent()) :
    VMComponent {


    companion object : ComponentFactory<NetworkCardComponent> {
        override fun create(): NetworkCardComponent {
            return NetworkCardComponent()
        }
    }
}