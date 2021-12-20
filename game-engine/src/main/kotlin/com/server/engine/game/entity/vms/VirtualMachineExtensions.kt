package com.server.engine.game.entity.vms

import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.VirtualSoftwareUpdateEvent
import com.server.engine.game.entity.vms.software.VirtualSoftware

fun VirtualMachine.addSoftware(software: VirtualSoftware) {
    if(has<HardDriveComponent>()) {
        val hdd = component<HardDriveComponent>()
        hdd.addSoftware(software)
        updateEvents.tryEmit(VirtualSoftwareUpdateEvent(this, software))
    }
}