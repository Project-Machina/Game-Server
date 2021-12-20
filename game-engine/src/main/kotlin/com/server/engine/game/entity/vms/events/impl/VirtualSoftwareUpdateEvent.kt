package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.UpdateEvent
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.packets.outgoing.VirtualSoftwareUpdateMessage

class VirtualSoftwareUpdateEvent(
    override val vm: VirtualMachine,
    override val source: VirtualSoftware
) : UpdateEvent<VirtualSoftware> {
    override fun handleEvent(player: Player) {
        player.session.sendMessage(VirtualSoftwareUpdateMessage(source))
    }
}