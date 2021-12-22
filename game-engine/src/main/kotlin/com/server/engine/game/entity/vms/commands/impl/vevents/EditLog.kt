package com.server.engine.game.entity.vms.commands.impl.vevents

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.version
import com.xenomachina.argparser.ArgParser

class EditLog(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "elog"

    val logId by parser.storing("-i", "Log ID") { toInt() }
    val logSource by parser.storing("-s", help = "Log Source")
    val logMessage by parser.storing("-m", help = "Log Message")
    val logTime: Long by parser.storing("-t", help = "Log Timestamp") { toLong() }

    override fun execute(): VirtualProcess {
        val events: VirtualEventsComponent = if(isRemote) {
            target.component()
        } else source.component()
        if(logId in events) {
            val log = events.events[logId]!!
            if(log.hiddenVersion > 0.0) {
                source.systemOutput.tryEmit(SystemAlert("Access Denied", source, "Log Edit", true))
                return VirtualProcess.NO_PROCESS
            }
            events.setEvent(log.eventId, VirtualEvent(logSource, logMessage, log.hiddenVersion, logTime))
        }
        return VirtualProcess.NO_PROCESS
    }

}