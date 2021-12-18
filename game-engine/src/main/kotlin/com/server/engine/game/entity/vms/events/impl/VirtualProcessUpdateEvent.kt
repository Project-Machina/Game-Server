package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.UpdateEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.packets.outgoing.VirtualProcessUpdateMessage

class VirtualProcessUpdateEvent(
    override val vm: VirtualMachine,
    override val source: VirtualProcess
) : UpdateEvent<VirtualProcess> {
    override fun handleEvent(player: Player) {
        val session = player.session
        session.sendMessage(VirtualProcessUpdateMessage(source))
    }
}