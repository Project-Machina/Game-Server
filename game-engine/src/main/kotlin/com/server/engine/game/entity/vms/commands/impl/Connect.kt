package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
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

    private val world: GameWorld by inject()

    override val name: String = "connect"


    override fun execute(): VirtualProcess {
        val pcm = source.component<VirtualProcessComponent>()

        return VirtualProcess("Connection", true)
    }

    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }

    /*override fun execute(source: VirtualMachine): String {
        val address = args.joinToString(" ") { it }
        val con = source.component<ConnectionComponent>()
        val parts = address.split(".")
        //domain
        try {
            if (parts.size == 2 && (parts[1] == "com" || parts[1] == "org" || parts[1] == "net")) {
                con.connect(address)
            } else if (parts.size == 4 && parts.all { it.toIntOrNull() != null && it.toInt() in 0..255 }) {
                con.connect(address)
            } else {
                return "Unknown Host"
            }
        } catch (e: Exception) {
            return "Unknown Host"
        }
        return ""
    }

    override fun event(source: VirtualMachine): VirtualEvent {
        val ip = world.vmToAddress[source] ?: "Unknown Source"
        return VirtualEvent(ip, " connected to")
    }*/
}