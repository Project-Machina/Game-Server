package com.server.engine.game.entity.vms.commands.impl.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.isLoggedIn
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.component
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.has
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.vlog
import com.xenomachina.argparser.ArgParser

class CompleteProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "fproc"

    val pid: Int by parser.positional("Process Identification Number (PID)") { toInt() }

    override suspend fun execute(): VirtualProcess {
        val pcm = source.component<VirtualProcessComponent>()
        if(pcm.activeProcesses.containsKey(pid)) {
            val pc = pcm.activeProcesses[pid]!!
            if(pc.isComplete) {
                pc.shouldComplete = true
                if(isRemote && target.isLoggedIn(source.address)) {
                    source.vlog("localhost", "Completed Process - ${pc.name} at ${target.address}")
                    target.vlog(source.address, "Completed Process - ${pc.name}")
                } else {
                    source.vlog("localhost", "Completed Process - ${pc.name}")
                }
            }
        }
        return VirtualProcess.NO_PROCESS
    }
}