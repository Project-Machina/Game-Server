package com.server.engine.game.entity.vms.events.impl

import com.server.engine.dispatchers.PlayerDispatcher
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.packets.outgoing.SystemAccountUpdateMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SystemRemoteLoginAlert(
    override val vm: VirtualMachine,
    override val source: VirtualMachine
) : SystemOutput<VirtualMachine> {

    override suspend fun handleEventForPlayer(player: Player, isRemote: Boolean) {
        val linkComp = player.component<VirtualMachineLinkComponent>()
        val target = source.component<SystemAccountComponent>()
        player.session.sendMessage(SystemAccountUpdateMessage(target.getActiveAccountFor(vm.address)))
        linkComp.remoteMonitorJobs.add(source.systemOutput.onEach {
            if (it.isRemote) {
                it.handleEventForPlayer(player, true)
            }
        }.launchIn(PlayerDispatcher))
        source.component<HardDriveComponent>().markDirty()
    }
}