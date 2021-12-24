package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.events.impl.SystemRemoteLogoutAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

class RemoteLogout(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine,
    override val target: VirtualMachine
) : VmCommand {

    override val name: String = "logout"
    override suspend fun execute(): VirtualProcess {
        val taccman = target.component<SystemAccountComponent>()
        if (taccman.logout(source.address)) {
            source.systemOutput.emit(SystemRemoteLogoutAlert(source, target))
        }
        return VirtualProcess.NO_PROCESS
    }

}