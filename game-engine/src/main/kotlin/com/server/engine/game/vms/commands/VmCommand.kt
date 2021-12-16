package com.server.engine.game.vms.commands

import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.vms.components.vevents.VirtualEvent
import com.xenomachina.argparser.ArgParser

interface VmCommand {

    val name: String
    val args: Array<String>
    val parser: ArgParser

    fun execute(vm: VirtualMachine) : String
    fun event(source: VirtualMachine): VirtualEvent

}