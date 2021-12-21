package com.server.engine.game.entity.vms.commands.impl.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.VirtualInfoEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.HideSoftwareComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.packets.outgoing.VirtualEventMessage
import com.server.engine.packets.outgoing.VirtualInformationMessage
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

    val hidderVersion: Double by parser.storing("--hdr", help = "The Version to hide the software with.") { toDouble() }.default(0.0)

    override fun execute(): VirtualProcess {
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()

        val hidderSoft = if(hidderVersion == 0.0) {
            sourceHDD.getBestSoftware("hdr")
        } else sourceHDD.getSoftwaresByExtensionAndVersion("hdr", hidderVersion).singleOrNull()

        if(hidderSoft == null) {
            source.updateEvents.tryEmit(VirtualInfoEvent(
                "No hider software found.",
                source
            ))
            return VirtualProcess.NO_PROCESS
        }
        if(hidderSoft.has<ProcessOwnerComponent>() && hidderSoft.component<ProcessOwnerComponent>().pid == -1) {
            source.updateEvents.tryEmit(VirtualInfoEvent(
                "No hider software running.",
                source
            ))
            return VirtualProcess.NO_PROCESS
        }

        val softwareToHide = if(isRemote) {
            targetHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion).singleOrNull()
        } else sourceHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion).singleOrNull()

        if(softwareToHide == null) {
            source.updateEvents.tryEmit(VirtualInfoEvent(
                "Unable to hide/find software.",
                source
            ))
            return VirtualProcess.NO_PROCESS
        }

        val threadCost = (softwareToHide.size / 500).toInt()
        val pc = VirtualProcess("Hide ${softwareToHide.fullName}")
        pc.singleton<OnFinishProcessComponent>(HideSoftwareComponent(
            threadCost,
            target = target,
            hiderSoftware = hidderSoft,
            softwareToHide = softwareToHide
        ))
        return pc
    }
}