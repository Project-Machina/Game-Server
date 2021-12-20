package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.addSoftware
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.game.entity.vms.events.impl.VirtualSoftwareUpdateEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Spawn(override val args: Array<String>, override val parser: ArgParser, override val source: VirtualMachine) : VmCommand {

    override val name: String = "spawn"

    val moreHardware by parser.flagging("-m", help = "Adds hardware").default(false)
    val moreStorage by parser.flagging("-s", help = "More Storage").default(false)

    val softwareName by parser.storing("-n", help = "Software Name") { replace('_', ' ') }.default("")
    val softwareVersion by parser.storing("-v", help = "Software Version") { toDouble() }.default(0.0)

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

        if(softwareName.isNotEmpty()) {
            val nameData = softwareName.split('.')
            val name = nameData[0]
            val ext = nameData[1]
            val soft = software(name, ext) {
                if(softwareVersion != 0.0) {
                    add(VersionedComponent().also { it.version = softwareVersion })
                }
            }
            source.addSoftware(soft)
        }

        return VirtualProcess.NO_PROCESS
    }

    override fun fireEvent(): VirtualEvent {
        TODO("Not yet implemented")
    }
}