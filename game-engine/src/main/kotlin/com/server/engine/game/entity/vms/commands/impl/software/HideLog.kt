package com.server.engine.game.entity.vms.commands.impl.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.logs.HideLogComponent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class HideLog(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "hidelg"

    val logId by parser.storing("-l", help = "Log ID") { toInt() }

    val hidderVersion: Double by parser.storing("--hdr", help = "The Version to hide the software with.") { toDouble() }
        .default(0.0)

    override suspend fun execute(): VirtualProcess {
        val taccman = target.component<SystemAccountComponent>()
        if(isLocal || taccman.canEditLogs(source.address)) {
            val targetLogs = target.component<SystemLogsComponent>()
            val sourceHDD = source.component<HardDriveComponent>()
            val hiderSoft = if(hidderVersion == 0.0) {
                sourceHDD.getBestRunningSoftware("hdr")
            } else sourceHDD.getBestRunningSoftwareByVersion("hdr", hidderVersion)

            if(hiderSoft == null) {
                source.alert("No hider software running.")
                return VirtualProcess.NO_PROCESS
            }
            val log = targetLogs.systemLogs[logId]

            if(log == null) {
                source.alert("Log not found.")
                return VirtualProcess.NO_PROCESS
            }
            val threadCost = 2
            val pc = VirtualProcess("Hiding Log $logId")
            pc.singleton<OnFinishProcessComponent>(HideLogComponent(
                threadCost,
                hiderSoft = hiderSoft,
                logId = log.logId,
                target = target
            ))
            return pc
        }

        return VirtualProcess.NO_PROCESS
    }
}