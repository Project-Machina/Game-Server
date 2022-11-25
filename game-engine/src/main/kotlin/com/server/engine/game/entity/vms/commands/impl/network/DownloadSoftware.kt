package com.server.engine.game.entity.vms.commands.impl.network

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.hasSpaceFor
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.NO_PROCESS
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.DownloadSoftwareComponent
import com.server.engine.game.entity.vms.software.canSeek
import com.server.engine.game.entity.vms.software.isHidden
import com.server.engine.game.entity.vms.software.version
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class DownloadSoftware(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {
    override val name: String = "download"

    val softwareName by parser.storing("-n", help = "Software Name") { replace('_', ' ') }
    val softwareVersion by parser.storing("-v", help = "Software Version") { toDouble() }.default(0.0)

    override suspend fun execute(): VirtualProcess {
        if (!source.has<HardDriveComponent>()) {
            return NO_PROCESS
        }
        if (!target.has<HardDriveComponent>()) {
            return NO_PROCESS
        }
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()
        val softs = targetHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion)
        if(softs.isEmpty()) {
            source.systemOutput.emit(
                SystemAlert(
                    title = "Download",
                    message = "Software does not exist",
                    vm = source
                )
            )
            return NO_PROCESS
        }
        if (softs.size > 1) {
            return NO_PROCESS
        }
        val soft = softs.firstOrNull() ?: return NO_PROCESS
        val seeker = sourceHDD.getBestRunningSoftware("skr")
        if (soft.isHidden() && seeker != null && !soft.canSeek(seeker.version)) {
            source.systemOutput.emit(
                SystemAlert(
                    title = "Download",
                    message = "Software does not exist",
                    vm = source
                )
            )
            return NO_PROCESS
        }
        if (!source.hasSpaceFor(soft)) {
            source.systemOutput.emit(SystemAlert(
                "Not enough available hard drive space",
                source,
                "Download"
            ))
            return NO_PROCESS
        }
        val downloadPc = VirtualProcess("Downloading ${soft.fullName}")
        downloadPc.singleton<OnFinishProcessComponent>(
            DownloadSoftwareComponent(
                0,
                3000L,
                soft,
                target
            )
        )
        return downloadPc
    }
}