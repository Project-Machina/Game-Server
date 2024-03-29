package com.server.engine.game.entity.vms.commands

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.impl.*
import com.server.engine.game.entity.vms.commands.impl.exploitation.Bruteforce
import com.server.engine.game.entity.vms.commands.impl.network.DownloadSoftware
import com.server.engine.game.entity.vms.commands.impl.network.UploadSoftware
import com.server.engine.game.entity.vms.commands.impl.process.CompleteProcess
import com.server.engine.game.entity.vms.commands.impl.process.KillProcess
import com.server.engine.game.entity.vms.commands.impl.process.PauseProcess
import com.server.engine.game.entity.vms.commands.impl.software.HideLog
import com.server.engine.game.entity.vms.commands.impl.software.HideSoftware
import com.server.engine.game.entity.vms.commands.impl.software.InstallSoftware
import com.server.engine.game.entity.vms.commands.impl.software.SeekSoftware
import com.server.engine.game.entity.vms.commands.impl.vevents.ClearLogs
import com.server.engine.game.entity.vms.commands.impl.vevents.DeleteLog
import com.server.engine.game.entity.vms.commands.impl.vevents.EditLog
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemProcessCreateAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.xenomachina.argparser.ArgParser

class CommandManager : VMComponent {

    val commands = mutableMapOf<String, (Array<String>, ArgParser, VirtualMachine, VirtualMachine) -> VmCommand>(
        "connect" to { a, p, s, _ -> Connect(a, p, s) },
        "fproc" to { a, p, s, t -> CompleteProcess(a, p, s, t) },
        "killproc" to { a, p, s, t -> KillProcess(a, p, s, t) },
        "pproc" to { a, p, s, _ -> PauseProcess(a, p, s) },
        "spawn" to { a, p, s, _ -> Spawn(a, p, s) },
        "install" to { a, p, s, t -> InstallSoftware(a, p, s, t) },
        "hide" to { a, p, s, t -> HideSoftware(a, p, s, t) },
        "seek" to { a, p, s, t -> SeekSoftware(a, p, s, t) },
        "echo" to { a, p, s, t -> Echo(a, p, s, t) },
        "lgcls" to { a, p, s, _ -> ClearLogs(a, p, s) },
        "elog" to { a, p, s, t -> EditLog(a, p, s, t) },
        "rmlg" to { a, p, s, _ -> DeleteLog(a, p, s) },
        "hidelg" to { a, p, s, t -> HideLog(a, p, s, t) },
        "login" to { a, p, s, _ -> RemoteLogin(a, p, s) },
        "logout" to { a, p, s, t -> RemoteLogout(a, p, s, t) },
        "bf" to { a, p, s, t -> Bruteforce(a, p, s, t) },
        "download" to { a, p, s, t -> DownloadSoftware(a, p, s, t) },
        "upload" to { a, p, s, t -> UploadSoftware(a, p, s, t) }
    )

    suspend fun execute(args: Array<String>, source: VirtualMachine, target: VirtualMachine = source) {
        val name = args[0]
        val commandArgs = args.copyOfRange(1, args.size)
        if (commands.containsKey(name)) {
            val parser = ArgParser(commandArgs)
            val vmCommand = commands[name]!!.invoke(commandArgs, parser, source, target)
            val pc = vmCommand.execute()
            if (pc === VirtualProcess.NO_PROCESS) {
                return
            }

            val pcm = source.component<VirtualProcessComponent>()
            val targetPcm = target.component<VirtualProcessComponent>()
            val mb = target.component<MotherboardComponent>()

            val requiredRAM = pc.ramCost + targetPcm.ramUsage

            if (requiredRAM >= mb.availableRam) {
                source.systemOutput.emit(SystemAlert("Not enough RAM", source, "Resource Alert"))
                return
            }

            if (pc.immediate || pc.isIndeterminate) {
                pcm.addProcess(pc)
            } else {
                val newTime = pcm.calculateRunningTime(pc.minimalRunningTime, pc.threadCost, mb.availableThreads, pcm.threadUsage)
                pc.preferredRunningTime = newTime
                pcm.addProcess(pc)
                source.systemOutput.emit(SystemProcessCreateAlert(source, pc, source !== target))
            }
        } else {
            println("No Command for $name")
        }
    }

}