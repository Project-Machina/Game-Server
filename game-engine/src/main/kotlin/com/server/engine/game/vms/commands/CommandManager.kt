package com.server.engine.game.vms.commands

import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.vms.commands.impl.Connect
import com.server.engine.game.vms.commands.impl.Echo
import com.server.engine.game.vms.commands.impl.IfConfig
import com.xenomachina.argparser.ArgParser

object CommandManager {

    val commands = mutableMapOf<String, (Array<String>, ArgParser) -> VmCommand>(
        "ifconfig" to { a, p -> IfConfig(a, p) },
        "echo" to { a, p -> Echo(a, p) },
        "connect" to { a, p -> Connect(a, p) }
    )

    fun execute(name: String, args: Array<String>, vm: VirtualMachine) : String {
        if(commands.containsKey(name)) {
            val cmd = commands[name]!!.invoke(args, ArgParser(args))
            return cmd.execute(vm)
        }
        return ""
    }

}