package com.server.engine.game.entity.vms

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.components.power.PoweredComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import kotlinx.serialization.json.JsonObject

interface VMComponent : Component {
    val upgrades: UpgradableComponent
        get() = UpgradableComponent
    val powerConsumption: PoweredComponent
        get() = PoweredComponent.NO_POWER

    suspend fun onTick(source: VirtualMachine) {}
}