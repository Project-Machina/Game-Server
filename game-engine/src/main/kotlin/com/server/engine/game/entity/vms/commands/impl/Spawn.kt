package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Spawn(override val args: Array<String>, override val parser: ArgParser, override val source: VirtualMachine) : VmCommand {

    override val name: String = "spawn"

    override val playerWhitelist: MutableList<String> = mutableListOf("javatar")

    val moreHardware by parser.flagging("-m", help = "Adds hardware").default(false)
    val moreStorage by parser.flagging("-s", help = "More Storage").default(false)

    override fun execute(): VirtualProcess {

        if(moreHardware) {
            val mb = source.component<MotherboardComponent>()
            mb.set("Spawned", 2048, Int.MAX_VALUE.toLong(), Int.MAX_VALUE)
        }
        if(moreStorage) {
            val rack = source.component<StorageRackComponent>()
            rack.maxCapacity = Int.MAX_VALUE.toLong()
            rack.availableSpace = rack.maxCapacity
        }

        return VirtualProcess.NO_PROCESS
    }

    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }
}