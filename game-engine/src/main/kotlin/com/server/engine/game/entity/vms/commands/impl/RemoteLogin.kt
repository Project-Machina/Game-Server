package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemRemoteLoginAlert
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.vlog
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.get
import com.xenomachina.argparser.ArgParser

class RemoteLogin(
    override val args: Array<String>,
    override val parser: ArgParser,
    override val source: VirtualMachine
) : VmCommand {

    override val name: String = "login"

    val address by parser.storing("-i", help = "Remote Address") { replace('_', ' ') }
    val username by parser.storing("-u", help = "Remote Username")
    val password by parser.storing("-p", help = "Remote Password")

    override suspend fun execute(): VirtualProcess {
        val world: GameWorld = get()
        val con = source.component<ConnectionComponent>()
        val addr = if(world.validateDomain(address)) {
            world.domainToAddress[address]
        } else address
        if(addr == null) {
            source.systemOutput.emit(SystemAlert("Address doest not exist", source, "Login"))
            return VirtualProcess.NO_PROCESS
        }
        if(con.remoteAddress.value != addr) {
            source.systemOutput.emit(SystemAlert("Please connect to the target before logging in.", source, "Login"))
            return VirtualProcess.NO_PROCESS
        }
        if(source.address == address) {
            source.systemOutput.emit(SystemAlert("Can't change accounts on linked machine.", source))
            return VirtualProcess.NO_PROCESS
        }
        val target = con.remoteVM
        val taccman = target.component<SystemAccountComponent>()
        if(taccman.login(source.address, username, password)) {
            val acc = taccman.getActiveAccountFor(source.address)!!
            if(!acc.isHidden()) {
                target.vlog(source.address, "Logged in as $username.")
            } else {
                target.vlog(source.address, "Logged in as unknown.")
            }
            source.systemOutput.emit(SystemRemoteLoginAlert(source, target))
            return VirtualProcess.NO_PROCESS
        }
        source.systemOutput.emit(SystemAlert("Access Denied", source, "Login", true))
        return VirtualProcess.NO_PROCESS
    }

}