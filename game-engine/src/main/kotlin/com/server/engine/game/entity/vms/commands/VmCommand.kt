package com.server.engine.game.entity.vms.commands

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser

interface VmCommand {

    val name: String
    val args: Array<String>
    val parser: ArgParser
    val source: VirtualMachine
    val target: VirtualMachine get() = source

    val isLocal: Boolean get() = source === target
    val isRemote: Boolean get() = source !== target

    suspend fun execute(): VirtualProcess
}