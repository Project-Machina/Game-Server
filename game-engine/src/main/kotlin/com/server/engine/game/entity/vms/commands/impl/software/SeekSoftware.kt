package com.server.engine.game.entity.vms.commands.impl.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.NO_PROCESS
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.SeekSoftwareComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class SeekSoftware(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "seek"

    val softwareName by parser.storing("-n", help = "Software name to seek.") { replace('_', ' ') }
    val softwareVersion by parser.storing("-v", help = "Software version to seek.") { toDouble() }

    val seekerVersion: Double by parser.storing("--skr", help = "The Version to seek the software with.") { toDouble() }
        .default(0.0)

    override suspend fun execute(): VirtualProcess {
        val taccman = target.component<SystemAccountComponent>()
        if (isLink || taccman.canExecuteSoftware(source.address)) {
            val sourceHDD = source.component<HardDriveComponent>()
            val targetHDD = target.component<HardDriveComponent>()

            val seekerSoft = if (seekerVersion == 0.0) {
                sourceHDD.getBestSoftware("skr")
            } else sourceHDD.getSoftwaresByExtensionAndVersion("skr", seekerVersion).singleOrNull()

            if (seekerSoft == null) {
                source.systemOutput.emit(
                    SystemAlert(
                        "No seeker software found.",
                        source
                    )
                )
                return NO_PROCESS
            }
            if (seekerSoft.has<ProcessOwnerComponent>() && seekerSoft.component<ProcessOwnerComponent>().pid == -1) {
                source.systemOutput.emit(
                    SystemAlert(
                        "No seeker software running.",
                        source
                    )
                )
                return NO_PROCESS
            }

            val softwareToSeek = targetHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion).singleOrNull()

            if (softwareToSeek == null) {
                source.systemOutput.emit(
                    SystemAlert(
                        "Unable to seek/find software.",
                        source
                    )
                )
                return NO_PROCESS
            }

            val threadCost = (softwareToSeek.size / 500).toInt()
            val pc = VirtualProcess("Seek ${softwareToSeek.fullName}")
            pc.singleton<OnFinishProcessComponent>(
                SeekSoftwareComponent(
                    threadCost,
                    target = target,
                    seekerSoftware = seekerSoft,
                    softwareToSeek = softwareToSeek
                )
            )
            return pc
        }
        source.alert("Access Denied", "Seek $softwareName", AlertType.ACCESS_DENIED)
        return NO_PROCESS
    }
}