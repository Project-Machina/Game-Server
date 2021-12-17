package com.server.engine.game.entity.vms.commands

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.impl.Connect
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.xenomachina.argparser.ArgParser

class CommandManager(val source: VirtualMachine) : VMComponent {

    val commands = mutableMapOf<String, (Array<String>, ArgParser, VirtualMachine, VirtualMachine) -> VmCommand>(
        "connect" to { a, p, s, t -> Connect(a, p, s, t) }
    )

    fun execute(args: Array<String>, target: VirtualMachine) {
        val name = args[0]
        val commandArgs = args.copyOfRange(1, args.size)
        if(commands.containsKey(name)) {
            val parser = ArgParser(commandArgs)
            val vmCommand = commands[name]!!.invoke(commandArgs, parser, source, target)
            val pc = vmCommand.execute()
            val pcm = source.component<VirtualProcessComponent>()
            if(pc.immediate) {
                pcm.activeProcesses.add(pc)
            } else {
                val newTime = pcm.calculateRunningTime(pc.minimalRunningTime, pc.threadCost)
                pc.preferredRunningTime = newTime
                pcm.activeProcesses.add(pc)
            }
        }
    }

}