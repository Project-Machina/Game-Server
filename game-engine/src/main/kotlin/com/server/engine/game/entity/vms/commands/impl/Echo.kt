package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class Echo(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "echo"

    override fun execute(): VirtualProcess {
        val msg = args.joinToString(" ") { it }

        if(isRemote) {
            target.systemOutput.tryEmit(SystemAlert(msg, target))
        } else {
            source.systemOutput.tryEmit(SystemAlert(msg, source))
        }

        return VirtualProcess.NO_PROCESS
    }
}