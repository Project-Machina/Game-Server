package com.server.engine.game.entity.vms.commands

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.impl.Connect
import com.server.engine.game.entity.vms.commands.impl.DummyProcess
import com.server.engine.game.entity.vms.commands.impl.Spawn
import com.server.engine.game.entity.vms.commands.impl.TestCommand
import com.server.engine.game.entity.vms.commands.impl.process.CompleteProcess
import com.server.engine.game.entity.vms.commands.impl.process.KillProcess
import com.server.engine.game.entity.vms.commands.impl.process.PauseProcess
import com.server.engine.game.entity.vms.commands.impl.software.InstallSoftware
import com.server.engine.game.entity.vms.commands.impl.vevents.DeleteLog
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.xenomachina.argparser.ArgParser

class CommandManager : VMComponent {

    val commands = mutableMapOf<String, (Array<String>, ArgParser, VirtualMachine, VirtualMachine) -> VmCommand>(
        "connect" to { a, p, s, t -> Connect(a, p, s, t) },
        "dummy" to { a, p, s, _ -> DummyProcess(a, p, s) },
        "test" to { a, p, s, _ -> TestCommand(a, p, s) },
        "fproc" to { a, p, s, _ -> CompleteProcess(a, p, s) },
        "killproc" to { a, p, s, _ -> KillProcess(a, p, s) },
        "pproc" to { a, p, s, _ -> PauseProcess(a, p, s) },
        "spawn" to { a, p, s, _ -> Spawn(a, p, s) },
        "rmlg" to { a, p, s, t -> DeleteLog(a, p, s, t) },
        "install" to { a, p, s, t -> InstallSoftware(a, p, s, t) }
    )

    fun execute(args: Array<String>, source: VirtualMachine, target: VirtualMachine) {
        val name = args[0]
        val commandArgs = args.copyOfRange(1, args.size)
        if (commands.containsKey(name)) {
            val parser = ArgParser(commandArgs)
            val vmCommand = commands[name]!!.invoke(commandArgs, parser, source, target)
            val pc = vmCommand.execute()
            if(pc === VirtualProcess.NO_PROCESS)
                return

            val pcm = source.component<VirtualProcessComponent>()
            val mb = source.component<MotherboardComponent>()

            val requiredRAM = pc.ramCost + pcm.ramUsage

            if(requiredRAM >= mb.availableRam) {
                return
            }

            if (pc.immediate || pc.isIndeterminate) {
                pcm.addProcess(pc)
            } else {
                val newTime = pcm.calculateRunningTime(pc.minimalRunningTime, pc.threadCost, mb.availableThreads)
                pc.preferredRunningTime = newTime
                pcm.addProcess(pc)
            }
        } else {
            println("No Command for $name")
        }
    }

}