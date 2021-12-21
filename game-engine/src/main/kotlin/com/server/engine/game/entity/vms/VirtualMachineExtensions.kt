package com.server.engine.game.entity.vms

import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.software.VirtualSoftware

fun VirtualMachine.addSoftware(vararg softwares: VirtualSoftware) {
    if(has<HardDriveComponent>()) {
        val hdd = component<HardDriveComponent>()
        softwares.forEach { software ->
            hdd.addSoftware(software)
        }
        systemOutput.tryEmit(SystemSoftwareAlert(this, hdd))
    }
}