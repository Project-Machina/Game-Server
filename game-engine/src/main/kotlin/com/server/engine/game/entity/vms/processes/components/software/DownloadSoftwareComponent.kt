package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.addSoftware
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.hasSpaceFor
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.vlog
import com.server.engine.game.world.GameWorld
import kotlinx.serialization.json.*

class DownloadSoftwareComponent(
    override var networkCost: Int,
    override var runningTime: Long,
    private var software: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    private var target: VirtualMachine = VirtualMachine.NULL_MACHINE,
) : OnFinishProcessComponent {
    override var ramCost: Long = 0
    override var threadCost: Int = 0

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        if(software === VirtualSoftware.NULL_SOFTWARE || target == VirtualMachine.NULL_MACHINE) {
            return
        }
        if(!target.has<HardDriveComponent>() || !source.has<HardDriveComponent>()) {
            return
        }
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()
        if(!targetHDD.hasSoftware(software.id())) {
            return
        }
        //Consider allowing override for feature of viruses
        if(sourceHDD.hasSoftware(software.id())) {
            return
        }
        if(!source.hasSpaceFor(software)) {
            source.systemOutput.emit(
                SystemAlert(
                    "Not enough hard drive space",
                    source,
                    "Download"
                )
            )
            return
        }
        val sourceConn = source.component<ConnectionComponent>()
        if(!sourceConn.isConnectedTo(target.address)) {
            source.systemOutput.emit(SystemAlert(
                "Connection lost... please reestablish",
                source,
                "Download",
            ))
            return
        }
        if(!source.addSoftware(software.copy())) {
            source.systemOutput.emit(SystemAlert(
                "Not enough hard drive space",
                source,
                "Download"
            ))
            return
        }
        source.vlog("localhost", "Downloaded ${software.fullName} from ${target.addSoftware()}")
        target.vlog(source.address, "Downloaded ${software.fullName}")
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            if (target !== VirtualMachine.NULL_MACHINE) {
                put("target", target.id.toString())
            }
            if (software !== VirtualSoftware.NULL_SOFTWARE) {
                put("software", software.saveComponents())
            }
        }
    }

    override fun load(json: JsonObject) {
        super.load(json)
        if (json.containsKey("target")) {
            val target = GameWorld.vmachine(json["target"]!!.jsonPrimitive.content)
            if (target != null) {
                this.target = target
            }
        }
        if (json.containsKey("software")) {
            software = SoftwareBuilder.fromJson(json["software"]!!.jsonObject)
        }
    }

    companion object : ComponentFactory<DownloadSoftwareComponent> {
        override fun create(): DownloadSoftwareComponent {
            return DownloadSoftwareComponent(0, 0)
        }
    }
}