package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.packets.outgoing.VirtualInformationMessage

class SystemAlert(val message: String, override val vm: VirtualMachine, val title: String = "Alert") : SystemOutput<Unit> {
    override val source: Unit = Unit
    override suspend fun handleEventForPlayer(player: Player) {
        println("Handling message!")
        player.session.sendMessage(VirtualInformationMessage(title, message))
    }
}