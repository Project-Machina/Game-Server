package com.server.engine.game.entity.vms.commands.impl.vevents

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.SystemLog
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class EditLog(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "elog"

    val logId by parser.storing("-i", help = "Log ID") { toInt() }
    val logSource by parser.storing("-s", help = "Log Source") { replace('_', ' ') }
    val logMessage by parser.storing("-m", help = "Log Message") { replace('_', ' ') }
    val logTime: Long by parser.storing("-t", help = "Log Timestamp") { toLong() }

    override suspend fun execute(): VirtualProcess {
        val events: SystemLogsComponent = target.component()
        if(logId in events) {
            val log = events.systemLogs[logId]!!
            if(log.hiddenVersion > 0.0) {
                source.alert("Access Denied", "Log Edit", AlertType.ACCESS_DENIED)
                return VirtualProcess.NO_PROCESS
            }
            events.setLog(log.logId, SystemLog(logSource, logMessage, log.hiddenVersion, logTime))
        }
        return VirtualProcess.NO_PROCESS
    }

}