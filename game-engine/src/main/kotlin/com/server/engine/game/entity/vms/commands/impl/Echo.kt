package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Echo(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "echo"

    val toTarget by parser.flagging("-t", help = "Send echo to target.").default(false)

    override suspend fun execute(): VirtualProcess {
        val msg = args.joinToString(" ") { it }
        source.alert(msg, "Echo", AlertType.ACCESS_GRANTED)
        return VirtualProcess.NO_PROCESS
    }
}