package com.server.engine.game.vms.commands.impl

import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.vms.commands.VmCommand
import com.xenomachina.argparser.ArgParser

class Echo(override val args: Array<String>, override val parser: ArgParser) : VmCommand {
    override val name: String = "echo"

    override fun execute(vm: VirtualMachine): String {
        return args.joinToString(separator = " ") { it }
    }
}