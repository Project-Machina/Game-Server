package com.server.engine.game.entity.vms

import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemLogAlert
import com.server.engine.game.entity.vms.events.impl.SystemParameter
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

fun VirtualMachine.vlog(source: String, msg: String, encryptedVersion: Double = 0.0) {
    if(has<VirtualEventsComponent>()) {
        val vevents = component<VirtualEventsComponent>()
        vevents.addEvent(VirtualEvent(source, msg, encryptedVersion))
    }
}

suspend fun VirtualMachine.alert(message: String, title: String = "Alert", type: AlertType = AlertType.INFORMATION) {
    systemOutput.emit(SystemAlert(message, this, title, type))
}

suspend fun VirtualMachine.fireSoftwareChange() {
    systemOutput.emit(SystemSoftwareAlert(this))
}

suspend fun VirtualMachine.fireLogChange() {
    systemOutput.emit(SystemLogAlert(this, this.component()))
}

suspend fun VirtualMachine.setParams(vararg params: Pair<String, Any>) {
    systemOutput.emit(SystemParameter(this, *params))
}

fun VirtualMachine.isLoggedIn(source: String): Boolean {
    return component<SystemAccountComponent>().isActive(source)
}