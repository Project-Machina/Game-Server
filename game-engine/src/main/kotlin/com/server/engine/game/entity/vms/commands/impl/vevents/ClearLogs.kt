package com.server.engine.game.entity.vms.commands.impl.vevents

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.logs.ClearLogsComponent
import com.xenomachina.argparser.ArgParser

class ClearLogs(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "lgcls"

    override suspend fun execute(): VirtualProcess {
        if(source.has<SystemLogsComponent>()) {
            val pc = VirtualProcess("Clearing Logs")
            val logs = source.component<SystemLogsComponent>()
            var threadCost = logs.systemLogs.size / 5
            if(threadCost <= 0) {
               threadCost = 1
            }
            pc.singleton<OnFinishProcessComponent>(ClearLogsComponent(threadCost))
            return pc
        }
        source.systemOutput.tryEmit(SystemAlert("Machine does not support logs", source))
        return VirtualProcess.NO_PROCESS
    }
}