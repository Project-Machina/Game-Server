package com.server.engine.game.entity.vms.commands.impl.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.NO_PROCESS
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.InstallSoftwareComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.NULL_SOFTWARE
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.util.*

class InstallSoftware(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "install"

    val softwareName by parser.storing("-n", help = "Software Name") { replace('_', ' ') }
    val softwareVersion by parser.storing("-v", help = "Software Version") { toDouble() }.default(0.0)

    val encryptWithBest by parser.flagging("-E", help = "Use best encryption software.").default(false)

    val remSoftwareName by parser.storing("-e", help = "Rem Software Name") { replace('_', ' ') }.default("")
    val remSoftwareVersion by parser.storing("-V", help = "Rem Software Version") { toDouble() }.default(0.0)

    override suspend fun execute(): VirtualProcess {
        val taccman = target.component<SystemAccountComponent>()
        if (isLink || taccman.canExecuteSoftware(source.address)) {
            val sourceHDD = source.component<HardDriveComponent>()
            val targetHDD = target.component<HardDriveComponent>()

            val soft = if (softwareVersion == 0.0) {
                targetHDD.getBestSoftware(softwareName.split(".")[1])
            } else {
                targetHDD.getSoftwareByNameAndVersion(softwareName, softwareVersion).singleOrNull()
            }
            if (soft == null)
                return NO_PROCESS

            val remSoft = if (encryptWithBest) {
                sourceHDD.getBestSoftware("rem")
            } else if (remSoftwareName.isNotEmpty() && remSoftwareName.endsWith(".rem") && remSoftwareVersion >= 1.0) {
                sourceHDD.getSoftwareByNameAndVersion(remSoftwareName, remSoftwareVersion).singleOrNull()
            } else null

            val name = "Installing ${soft.fullName}"
            val remVersion = remSoft?.component<VersionedComponent>()?.version ?: 0.0

            val sizeThreadCost = (soft.size / 500).toInt()
            val remThreadCost = if (remVersion == 0.0) 0 else remVersion.toInt() / 5
            val threadCost = sizeThreadCost + remThreadCost

            val pc = VirtualProcess(name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
            pc.singleton<OnFinishProcessComponent>(InstallSoftwareComponent(
                threadCost = threadCost,
                software = soft,
                remSoftware = remSoft ?: NULL_SOFTWARE,
                target = target
            ))
            return pc
        }
        source.alert("Access Denied", "Install $softwareName",  AlertType.ACCESS_DENIED)
        return NO_PROCESS
    }

}