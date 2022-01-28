package com.server.engine.game.entity.vms.commands.impl.vevents

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class DeleteLog(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "rmlg"

    val logId by parser.storing("-i", help = "Event IDs") { toInt() }

    override suspend fun execute(): VirtualProcess {
        if (source.has<SystemLogsComponent>()) {
            val events = source.component<SystemLogsComponent>()
            if(logId in events) {
                events.remove(logId)
            }
        } else {
            source.systemOutput.tryEmit(SystemAlert("Machine does not support logs", source))
        }
        return VirtualProcess.NO_PROCESS
    }
}