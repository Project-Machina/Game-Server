package com.server.engine.game.entity.vms

import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.components.vevents.SystemLog
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemLogAlert
import com.server.engine.game.entity.vms.events.impl.SystemParameter
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.InstallSoftwareComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import java.util.*

fun VirtualMachine.addSoftware(vararg softwares: VirtualSoftware) : Boolean {
    if (has<HardDriveComponent>() && softwares.isNotEmpty()) {
        val hdd = component<HardDriveComponent>()
        val soft = softwares.first()
        val softs = softwares.copyOfRange(1, softwares.size)
        if(!hasSpaceFor(soft, *softs)) {
            return false
        }
        softwares.forEach { software ->
            hdd.addSoftware(software)
        }
        systemOutput.tryEmit(SystemSoftwareAlert(this, hdd))
        return true
    }
    return false
}

fun VirtualMachine.hasSpaceFor(soft: VirtualSoftware, vararg softs: VirtualSoftware) : Boolean {
    if(!has<StorageRackComponent>() || !has<HardDriveComponent>()) return false
    val size = listOf(soft, *softs).sumOf { it.size }
    val storage = component<StorageRackComponent>()
    val hdd = component<HardDriveComponent>()
    return (hdd.driveUsage + size) <= storage.availableSpace
}

fun VirtualMachine.vlog(source: String, msg: String, encryptedVersion: Double = 0.0) {
    if (has<SystemLogsComponent>()) {
        val vevents = component<SystemLogsComponent>()
        vevents.addLog(SystemLog(source, msg, encryptedVersion))
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

fun VirtualMachine.directInstall(
    software: VirtualSoftware,
    remSoftware: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE
) {
    val remVersion = if(remSoftware.has<VersionedComponent>()) remSoftware.component<VersionedComponent>().version else 0.0
    val sizeThreadCost = (software.size / 500).toInt()
    val remThreadCost = if (remVersion == 0.0) 0 else remVersion.toInt() / 5
    val threadCost = sizeThreadCost + remThreadCost
    val pc =
        VirtualProcess(name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, true)
    pc.singleton<OnFinishProcessComponent>(
        InstallSoftwareComponent(
            threadCost = threadCost,
            software = software,
            remSoftware = remSoftware,
            target = this
        )
    )
    val targetPcm = component<VirtualProcessComponent>()
    val mb = component<MotherboardComponent>()
    val requiredRAM = pc.ramCost + targetPcm.ramUsage
    if (requiredRAM >= mb.availableRam) {
        return
    }
    targetPcm.addProcess(pc)
}