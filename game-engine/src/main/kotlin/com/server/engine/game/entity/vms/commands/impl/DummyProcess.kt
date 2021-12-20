package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour
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
        return VirtualProcess(
            "Dummy",
            isIndeterminate = isIndeterminate,
            behaviours = listOf(VirtualProcessBehaviour.createAnonymous(5000) { vm, pc ->
                println("Dummy Process behaviour!")
            })
        )
    }
}