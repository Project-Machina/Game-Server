package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.components.ComponentManager.Companion.with
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.ProcessComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.with
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class DummyProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "dumb"

    val isIndeterminate: Boolean by parser.flagging("-i", help = "Spawns a Indeterminate").default(false)

    override fun execute(): VirtualProcess {
        val pc = VirtualProcess("Dummy", isIndeterminate = isIndeterminate)
        pc.with(ProcessComponent.createAnonymous { _, _ ->
            println("Dummy Process Component")
        })
        return pc
    }
}