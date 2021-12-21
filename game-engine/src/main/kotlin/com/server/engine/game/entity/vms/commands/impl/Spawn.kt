package com.server.engine.game.entity.vms.commands.impl

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.addSoftware
import com.server.engine.game.entity.vms.commands.VmCommand
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import kotlin.random.Random

class Spawn(override val args: Array<String>, override val parser: ArgParser, override val source: VirtualMachine) : VmCommand {

    override val name: String = "spawn"

    val moreHardware by parser.flagging("-m", help = "Adds hardware").default(false)
    val moreStorage by parser.flagging("-s", help = "More Storage").default(false)

    val softSuite by parser.flagging("--suite", help = "Spawns software suite.")
    val massSoftTest by parser.flagging("--mass", help = "Spawns a much of random software.")
    val clearHDD by parser.flagging("--cls", help = "Quicly formats hard drive.")

    val overclock by parser.flagging("--xpu",help = "Overkill CPU")

    val softwareName by parser.storing("-n", help = "Software Name") { replace('_', ' ') }.default("")
    val softwareVersion by parser.storing("-v", help = "Software Version") { toDouble() }.default(0.0)

    override fun execute(): VirtualProcess {

        if(overclock) {
            val mb = source.component<MotherboardComponent>()
            mb.cpuCapacity = 5000
            mb.availableThreads = 5000
        }

        if(clearHDD) {
            val hdd = source.component<HardDriveComponent>()
            hdd.softwares.clear()
            source.systemOutput.tryEmit(SystemSoftwareAlert(source))
        }

        if(massSoftTest) {
            val softs = mutableListOf<VirtualSoftware>()
            val exts = arrayOf("crc", "hash", "av", "vspam", "fwl", "skr", "hdr")
            repeat(500) {
                val s = software("Software $it", exts.random())
                val random: Double = Random.nextDouble(100.0)
                val version = String.format("%.1f", random).toDouble()
                s.addComponent(VersionedComponent().apply { this.version = version })
                softs.add(s)
            }
            source.addSoftware(*softs.toTypedArray())
        }

        if(softSuite) {
            val cracker = software("Cracker", "crc") {
                +VersionedComponent
            }
            val hasher = software("Hasher", "hash") {
                +VersionedComponent
            }
            val firewall = software("Firewall", "fwl") {
                +VersionedComponent
            }
            val hidder = software("Hidder", "hdr") {
                +VersionedComponent
            }
            val seeker = software("Seeker", "skr") {
                +VersionedComponent
            }
            source.addSoftware(cracker, hasher, firewall, hidder, seeker)
        }

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
}