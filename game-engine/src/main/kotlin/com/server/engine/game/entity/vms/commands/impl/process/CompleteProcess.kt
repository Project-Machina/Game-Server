package com.server.engine.game.entity.vms.commands.impl.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.vlog
import com.xenomachina.argparser.ArgParser

class CompleteProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "fproc"

    val pid: Int by parser.positional("Process Identification Number (PID)") { toInt() }

    override fun execute(): VirtualProcess {
        val pcm = source.component<VirtualProcessComponent>()
        if(pcm.activeProcesses.containsKey(pid)) {
            val pc = pcm.activeProcesses[pid]!!
            if(pc.isComplete) {
                pc.shouldComplete = true
                source.vlog("localhost", "Completed Process $pid - ${pc.name}")
            }
        }
        return VirtualProcess.NO_PROCESS
    }
}