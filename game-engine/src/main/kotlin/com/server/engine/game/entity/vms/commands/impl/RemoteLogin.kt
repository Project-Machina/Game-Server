package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.alert
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.game.entity.vms.events.impl.SystemRemoteLoginAlert
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
            source.alert("Address doest not exist", "Login")
            return VirtualProcess.NO_PROCESS
        }
        //TODO add this back when domain protection is written
        /*if(con.remoteAddress.value != addr) {
            source.alert("Please connect to the target before logging in.", "Login")
            return VirtualProcess.NO_PROCESS
        }*/
        if(source.address == address) {
            source.alert("Can't change accounts on linked machine.")
            return VirtualProcess.NO_PROCESS
        }
        val target = con.remoteVM
        if(target == null) {
            source.alert("Not connected to a IP.")
            return VirtualProcess.NO_PROCESS
        }
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
        source.alert("Access Denied", "Login", AlertType.ACCESS_DENIED)
        return VirtualProcess.NO_PROCESS
    }

}