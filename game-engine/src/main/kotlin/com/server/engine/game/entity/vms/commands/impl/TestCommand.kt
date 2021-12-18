package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class TestCommand(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "test"

    override fun execute(): VirtualProcess {
        println("Test Command Fired!")
        return VirtualProcess("test", true)
    }

    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }
}