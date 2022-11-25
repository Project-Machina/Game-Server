package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.NULL_MACHINE
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.NULL_SOFTWARE
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.replace
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.entity.vms.software.isRunning
import com.server.engine.game.world.GameWorld.Companion.vmachine
import com.server.engine.utilities.jobj
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class HideSoftwareComponent(
    override var threadCost: Int,
    override var networkCost: Int = 0,
    override var ramCost: Long = 0,
    override var runningTime: Long = 3000,
    var target: VirtualMachine = NULL_MACHINE,
    var hiderSoftware: VirtualSoftware = NULL_SOFTWARE,
    var softwareToHide: VirtualSoftware = NULL_SOFTWARE
) : OnFinishProcessComponent {

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        if(target === NULL_MACHINE) {
            return
        }
        if(hiderSoftware == NULL_SOFTWARE || softwareToHide == NULL_SOFTWARE) {
            return
        }
        if(source !== target) {
            val sourceConn = source.component<ConnectionComponent>()
            if(!sourceConn.isConnectedTo(target.address)) {
                source.systemOutput.emit(SystemAlert(
                    "Connection lost... please reestablish",
                    source,
                    "Hide",
                ))
                return
            }
        }
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()
        val hasSoftware = targetHDD.hasSoftware(softwareToHide.id())
        if (hasSoftware && sourceHDD.hasSoftware(hiderSoftware.id()) && !softwareToHide.isRunning()) {
            softwareToHide.replace(VisibleComponent()) {
                hiddenVersion = hiderSoftware.component<VersionedComponent>().version
            }
            target.systemOutput.emit(
                SystemSoftwareAlert(
                    target,
                    targetHDD
                )
            )
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            put("target", target.id.toString())
            put("hiderSoftware", hiderSoftware.saveComponents())
            put("softwareToHide", softwareToHide.saveComponents())
        }
    }

    override fun load(json: JsonObject) {
        super.load(json["stats"]!!.jsonObject)
        val target = vmachine(json.string("target"))
        val hiderSoftware = software()
        val softwareToHide = software()
        hiderSoftware.loadComponents(json.jobj("hiderSoftware"))
        softwareToHide.loadComponents(json.jobj("softwareToHide"))
        if (hiderSoftware.name.isNotEmpty()) {
            this.hiderSoftware = hiderSoftware
        }
        if (softwareToHide.name.isNotEmpty()) {
            this.softwareToHide = softwareToHide
        }
        if (target != null) {
            this.target = target
        }
    }

    companion object : ComponentFactory<HideSoftwareComponent> {
        override fun create(): HideSoftwareComponent {
            return HideSoftwareComponent(0)
        }
    }
}