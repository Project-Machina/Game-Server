package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.packets.outgoing.VirtualProcessCreateMessage

class SystemProcessCreateAlert(
    override val vm: VirtualMachine,
    override val source: VirtualProcess
) : SystemOutput<VirtualProcess> {
    override suspend fun handleEventForPlayer(player: Player) {
        player.session.sendMessage(VirtualProcessCreateMessage(source))
    }
}