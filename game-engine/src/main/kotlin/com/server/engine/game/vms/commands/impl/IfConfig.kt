package com.server.engine.game.vms.commands.impl

import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.vms.VirtualMachine.Companion.component
import com.server.engine.game.vms.commands.VmCommand
import com.server.engine.game.vms.components.connection.ConnectionComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import com.xenomachina.argparser.ArgParser

class IfConfig(override val args: Array<String>, override val parser: ArgParser) : VmCommand {

    val world: GameWorld by inject()

    override val name: String = "ifconfig"

    override fun execute(vm: VirtualMachine): String {
        val myIP = world.vmToAddress[vm] ?: "Unknown"

        val targetVM = vm.component<ConnectionComponent>()
        val remoteIP = targetVM.remoteIP.value

        return "$myIP:$remoteIP"
    }
}