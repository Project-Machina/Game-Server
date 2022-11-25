package com.server.engine.game.entity.vms.components.connection

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.events.impl.SystemRemoteLogoutAlert
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Don't save this, if server goes down everything gets disconnected!
 */

class ConnectionComponent(val source: VirtualMachine) : VMComponent {

    private val world: GameWorld by inject()

    val remoteAddress = MutableStateFlow("localhost")
    var isConnectionByDomain: Boolean = false
        private set

    val remoteVM: VirtualMachine?
        get() = world.getVirtualMachine(remoteAddress.value)

    fun connect(address: String) : Boolean {
        val isDomain = world.validateDomain(address)
        if(isDomain || world.publicVirtualMachines.containsKey(address)) {
            remoteVM?.let {
                val accman = it.component<SystemAccountComponent>()
                if(accman.isActive(source.address)) {
                    accman.logout(source.address)
                    source.systemOutput.tryEmit(SystemRemoteLogoutAlert(source, it))
                }
            }
            remoteAddress.value = address
            isConnectionByDomain = isDomain
            return true
        }
        return false
    }

    fun disconnect() {
        if (remoteAddress.value != "First Whois.com") {
            remoteAddress.value = "First Whois.com"
        }
    }

    fun isConnectedTo(address: String) : Boolean {
        return if(isConnectionByDomain && world.validateDomain(address)) {
            remoteAddress.value == address
        } else if(isConnectionByDomain && world.addressToDomain.containsKey(address)) {
            remoteAddress.value == world.addressToDomain[address]
        } else {
            remoteAddress.value == address
        }
    }
}