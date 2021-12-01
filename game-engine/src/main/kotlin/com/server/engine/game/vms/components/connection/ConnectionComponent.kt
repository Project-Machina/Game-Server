package com.server.engine.game.vms.components.connection

import com.server.engine.game.components.managers.SimpleComponentManager
import com.server.engine.game.vms.UpgradableComponent
import com.server.engine.game.vms.VMComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject

class ConnectionComponent(override val upgrades: UpgradableComponent = UpgradableComponent) : SimpleComponentManager(), VMComponent {

    private val world: GameWorld by inject()

    fun connect(address: String) {
        if(world.publicVirtualMachines.containsKey(address)) {

        }
    }

}