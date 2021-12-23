package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.events.impl.SystemServePageAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class Connect(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {
    override val name: String = "connect"
    override suspend fun execute(): VirtualProcess {
        val address = args.joinToString(" ") { it }
        val con = source.component<ConnectionComponent>()
        val parts = address.split(".")
        try {
            if (parts.size == 2 && (parts[1] == "com" || parts[1] == "org" || parts[1] == "net")) {
                if(con.connect(address)) {
                    source.systemOutput.tryEmit(SystemServePageAlert(source, con.remoteVM))
                }
            } else if (parts.size == 4 && parts.all { it.toIntOrNull() != null && it.toInt() in 0..255 }) {
                if(con.connect(address)) {
                    source.systemOutput.tryEmit(SystemServePageAlert(source, con.remoteVM))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return VirtualProcess.NO_PROCESS
    }
}