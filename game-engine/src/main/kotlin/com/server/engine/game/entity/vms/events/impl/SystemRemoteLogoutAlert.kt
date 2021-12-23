package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.packets.outgoing.SystemAccountUpdateMessage

class SystemRemoteLogoutAlert(
    override val vm: VirtualMachine,
    override val source: VirtualMachine
) : SystemOutput<VirtualMachine> {
    override suspend fun handleEventForPlayer(player: Player, isRemote: Boolean) {
        val linkComp = player.component<VirtualMachineLinkComponent>()
        linkComp.stopRemoteMonitorJobs()
        player.session.sendMessage(SystemAccountUpdateMessage())
    }
}