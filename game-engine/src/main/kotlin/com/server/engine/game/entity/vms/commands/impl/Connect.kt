package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import com.xenomachina.argparser.ArgParser

class Connect(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {
    override val name: String = "connect"
    override fun execute(): VirtualProcess {
        val address = args.joinToString(" ") { it }
        val con = source.component<ConnectionComponent>()
        val parts = address.split(".")
        try {
            if (parts.size == 2 && (parts[1] == "com" || parts[1] == "org" || parts[1] == "net")) {
                con.connect(address)
            } else if (parts.size == 4 && parts.all { it.toIntOrNull() != null && it.toInt() in 0..255 }) {
                con.connect(address)
            }
        } catch (_: Exception) {}
        return VirtualProcess.NO_PROCESS
    }
    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }
}