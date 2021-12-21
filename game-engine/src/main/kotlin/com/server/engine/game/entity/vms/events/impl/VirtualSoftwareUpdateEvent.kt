package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.UpdateEvent
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.entity.vms.software.isHidden
import com.server.engine.game.entity.vms.software.isRunning
import com.server.engine.packets.outgoing.VirtualSoftwareUpdateMessage

class VirtualSoftwareUpdateEvent(
    override val vm: VirtualMachine,
    override val source: HardDriveComponent = vm.component()
) : UpdateEvent<HardDriveComponent> {
    override fun handleEventForPlayer(player: Player) {
        val softs = mutableListOf<VirtualSoftware>()
        val hdd = vm.component<HardDriveComponent>()
        val seeker = hdd.getBestSoftware("skr")
        val isSeekRunning = seeker?.isRunning() ?: false
        val skrVersion = seeker?.component<VersionedComponent>()?.version ?: 0.0

        source.softwares.values.forEach {
            if(it.has<VisibleComponent>()) {
                val hidderVersion = it.component<VisibleComponent>().hiddenVersion
                val isHidden = hidderVersion > 0.0
                val isVisible = isHidden && isSeekRunning && skrVersion >= hidderVersion
                if(isVisible)
                    softs.add(it)
            } else {
                softs.add(it)
            }
        }

        player.session.sendMessage(VirtualSoftwareUpdateMessage(softs))
    }
}
