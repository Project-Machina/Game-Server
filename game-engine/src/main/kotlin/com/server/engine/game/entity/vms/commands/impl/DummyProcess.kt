package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour
import com.xenomachina.argparser.ArgParser

class DummyProcess(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "dumb"

    override fun execute(): VirtualProcess {
        return VirtualProcess("Dummy", behaviours = listOf(VirtualProcessBehaviour.createAnonymous(5000) {
            println("Dummy Process behaviour!")
        }))
    }

    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }
}