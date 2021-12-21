package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.UpdateEvent
import com.server.engine.packets.outgoing.VirtualInformationMessage

class VirtualInfoEvent(val message: String, override val vm: VirtualMachine, val title: String = "Alert") : UpdateEvent<Unit> {
    override val source: Unit = Unit
    override fun handleEventForPlayer(player: Player) {
        player.session.sendMessage(VirtualInformationMessage(title, message))
    }
}