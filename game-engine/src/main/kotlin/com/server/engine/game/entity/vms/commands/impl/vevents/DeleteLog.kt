package com.server.engine.game.entity.vms.commands.impl.vevents

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class DeleteLog(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "rmlg"

    val eventIds by parser.adding("-i", help = "Event IDs") { toInt() }

    override fun execute(): VirtualProcess {
        val threadCost = eventIds.size / 5
        TODO()
    }
}