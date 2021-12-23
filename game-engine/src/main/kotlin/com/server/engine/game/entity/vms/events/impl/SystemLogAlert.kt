package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.isRunning
import com.server.engine.packets.outgoing.VirtualLogUpdateMessage

class SystemLogAlert(
    override val vm: VirtualMachine,
    override val source: VirtualEventsComponent,
) : SystemOutput<VirtualEventsComponent> {

    override val isRemote: Boolean = true

    override suspend fun handleEventForPlayer(player: Player, isRemote: Boolean) {
        val logs = mutableListOf<VirtualEvent>()
        if(source.events.isNotEmpty()) {
            val hdd = vm.component<HardDriveComponent>()
            val dlog = hdd.getBestSoftware("dlog")
            val dlogVersion = if(dlog != null && dlog.isRunning()) {
                dlog.component<VersionedComponent>().version
            } else 0.0
            for (log in source.events.values) {
                if(dlogVersion >= log.hiddenVersion) {
                    logs.add(log)
                }
            }
        }
        player.session.sendMessage(VirtualLogUpdateMessage(logs, isRemote))
    }
}