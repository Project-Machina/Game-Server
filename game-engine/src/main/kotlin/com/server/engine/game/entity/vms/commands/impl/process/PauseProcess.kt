package com.server.engine.game.entity.vms.commands.impl.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.xenomachina.argparser.ArgParser

class PauseProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "pproc"

    val pid: Int by parser.positional("Process Identification Number (PID)") { toInt() }

    override fun execute(): VirtualProcess {
        val pcm = source.component<VirtualProcessComponent>()
        if(pcm.activeProcesses.containsKey(pid)) {
            val pc = pcm.activeProcesses[pid]!!
            pc.isPaused = !pc.isPaused
        }
        return VirtualProcess.NO_PROCESS
    }

}