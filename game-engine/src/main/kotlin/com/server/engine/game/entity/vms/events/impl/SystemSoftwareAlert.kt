package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.entity.vms.software.isRunning
import com.server.engine.packets.outgoing.VirtualSoftwareUpdateMessage

class SystemSoftwareAlert(
    override val vm: VirtualMachine,
    override val source: HardDriveComponent = vm.component()
) : SystemOutput<HardDriveComponent> {
    override suspend fun handleEventForPlayer(player: Player) {
        val softs = mutableListOf<VirtualSoftware>()
        val hdd = vm.component<HardDriveComponent>()
        val seeker = hdd.getBestSoftware("skr")
        val isSeekRunning = seeker?.isRunning() ?: false
        val skrVersion = seeker?.component<VersionedComponent>()?.version ?: 0.0
        source.softwares.values.forEach {
            if(it.has<VisibleComponent>() && it.component<VisibleComponent>().hiddenVersion > 0.0) {
                val hidderVersion = it.component<VisibleComponent>().hiddenVersion
                val isVisible = isSeekRunning && skrVersion >= hidderVersion
                if(isVisible)
                    softs.add(it)
            } else {
                softs.add(it)
            }
        }
        player.session.sendMessage(VirtualSoftwareUpdateMessage(softs))
    }
}
