package com.server.engine.game.entity.vms.components.connection

import com.server.engine.game.components.managers.SimpleComponentManager
import com.server.engine.game.entity.vms.UpgradableComponent
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class ConnectionComponent(override val upgrades: com.server.engine.game.entity.vms.UpgradableComponent = com.server.engine.game.entity.vms.UpgradableComponent) : SimpleComponentManager(),
    com.server.engine.game.entity.vms.VMComponent {

    private val world: GameWorld by inject()

    val remoteIP = MutableStateFlow("localhost")
    val domain = MutableStateFlow("none")

    val remoteVM: VirtualMachine
        get() {
        if(domain.value != "none" && world.validateDomain(domain.value)) {
            return world.publicVirtualMachines[world.domainToAddress[domain.value]!!]!!
        }
        return world.publicVirtualMachines[remoteIP.value]!!
    }

    fun isConnected() = remoteIP.value != "localhost"

    fun canAttackVirtualMachine() : Boolean {
        val domain = domain.value
        if(domain != "none" && domain.startsWith(".com")) {
            return false
        }
        return true
    }

    fun connect(address: String) : Boolean {
        if(world.validateDomain(address)) {
            val ip = world.domainToAddress[address]
            if(ip != null) {
                remoteIP.value = ip
                domain.value = address
                return true
            }
        } else if(world.publicVirtualMachines.containsKey(address)) {
            remoteIP.value = address
            return true
        }
        return false
    }

    fun disconnect() {
        remoteIP.value = "localhost"
    }
}