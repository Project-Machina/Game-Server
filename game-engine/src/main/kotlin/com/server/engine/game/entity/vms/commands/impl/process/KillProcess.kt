package com.server.engine.game.entity.vms.commands.impl.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.vlog
import com.xenomachina.argparser.ArgParser

class KillProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "killproc"

    val pid: Int by parser.positional("Process Identification Number (PID)") { toInt() }

    override suspend fun execute(): VirtualProcess {
        val pcm = target.component<VirtualProcessComponent>()
        if(pcm.activeProcesses.containsKey(pid)){
            val pc = pcm.activeProcesses[pid]!!
            pc.isKilled = true
            if(isRemote) {
                source.vlog("localhost", "Killed Process - ${pc.name} at ${target.address}")
                target.vlog(source.address, "Killed Process - ${pc.name}")
            } else {
                source.vlog("localhost", "Killed $pid - ${pc.name}")
            }
        }
        return VirtualProcess.NO_PROCESS
    }
}