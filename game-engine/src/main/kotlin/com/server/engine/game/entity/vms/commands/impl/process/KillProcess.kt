package com.server.engine.game.entity.vms.commands.impl.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.has
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.SoftwareLinkComponent
import com.server.engine.game.entity.vms.vlog
import com.xenomachina.argparser.ArgParser

class KillProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "killproc"

    val pid: Int by parser.positional("Process Identification Number (PID)") { toInt() }


    override fun execute(): VirtualProcess {
        val pcm = source.component<VirtualProcessComponent>()
        if(pcm.activeProcesses.containsKey(pid)){
            val pc = pcm.activeProcesses[pid]!!
            pc.isKilled = true
            source.vlog("localhost", "Killed $pid - ${pc.name}")
        }
        return VirtualProcess.NO_PROCESS
    }
}