package com.server.engine.game.entity.vms.commands.impl.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccount
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
import com.server.engine.game.entity.vms.processes.components.software.HideSoftwareComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.isRunning
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class HideSoftware(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "hide"

    val softwareName by parser.storing("-n", help = "Software name to hide.") { replace('_', ' ') }
    val softwareVersion by parser.storing("-v", help = "Software version to hide.") { toDouble() }

    val hidderVersion: Double by parser.storing("--hdr", help = "The Version to hide the software with.") { toDouble() }
        .default(0.0)

    override suspend fun execute(): VirtualProcess {
        val taccman = target.component<SystemAccountComponent>()
        if (isLocal || taccman.canExecuteSoftware(source.address)) {
            val sourceHDD = source.component<HardDriveComponent>()
            val targetHDD = target.component<HardDriveComponent>()

            val hidderSoft = if (hidderVersion == 0.0) {
                sourceHDD.getBestSoftware("hdr")
            } else sourceHDD.getSoftwaresByExtensionAndVersion("hdr", hidderVersion).singleOrNull()

            if (hidderSoft == null) {
                source.systemOutput.tryEmit(
                    SystemAlert(
                        "No hider software found.",
                        source
                    )
                )
                return NO_PROCESS
            }
            if (!hidderSoft.isRunning()) {
                source.systemOutput.tryEmit(
                    SystemAlert(
                        "No hider software running.",
                        source
                    )
                )
                return NO_PROCESS
            }
            val softwareToHide = targetHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion).singleOrNull()
            if (softwareToHide == null) {
                source.systemOutput.tryEmit(
                    SystemAlert(
                        "Unable to hide/find software.",
                        source
                    )
                )
                return NO_PROCESS
            }

            val threadCost = (softwareToHide.size / 500).toInt()
            val pc = VirtualProcess("Hide ${softwareToHide.fullName}")
            pc.singleton<OnFinishProcessComponent>(
                HideSoftwareComponent(
                    threadCost,
                    target = target,
                    hiderSoftware = hidderSoft,
                    softwareToHide = softwareToHide
                )
            )
            return pc
        }
        source.alert("Access Denied", "Hide $softwareName", AlertType.ACCESS_DENIED)
        return NO_PROCESS
    }
}