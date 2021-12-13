package com.server.engine.game.vms.components.connection

import com.server.engine.game.components.managers.SimpleComponentManager
import com.server.engine.game.vms.UpgradableComponent
import com.server.engine.game.vms.VMComponent
import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class ConnectionComponent(override val upgrades: UpgradableComponent = UpgradableComponent) : SimpleComponentManager(), VMComponent {

    private val world: GameWorld by inject()

    val remoteIP = MutableStateFlow("localhost")

    val remoteVM: VirtualMachine get() = world.publicVirtualMachines[remoteIP.value]!!

    fun connect(address: String) : Boolean {
        if(world.publicVirtualMachines.containsKey(address)) {
            remoteIP.value = address
            return true
        }
        return false
    }

    fun isConnected() = remoteIP.value != "localhost"

    fun disconnect() {
        remoteIP.value = "localhost"
    }
}