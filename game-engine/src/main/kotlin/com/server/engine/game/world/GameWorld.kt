package com.server.engine.game.world

import com.server.engine.game.entity.character.npcs.Npc
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.inject
import com.server.engine.game.vms.VirtualMachine

class GameWorld {

    val players = mutableMapOf<String, Player>()
    val npcs = mutableMapOf<String, Npc>()

    val publicVirtualMachines = mutableMapOf<String, VirtualMachine>()

    val ip: InternetProtocolManager by inject()

    fun assignAddress(vm: VirtualMachine, address: String = "") {
        val addr = if(address.isEmpty() || address.isBlank()) {
            ip.reserveAddress()
        } else address
        publicVirtualMachines.putIfAbsent(addr, vm)
    }

}