package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.VirtualSoftwareUpdateEvent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
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
    var target: VirtualMachine = VirtualMachine.NULL_MACHINE,
    var hiderSoftware: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    var softwareToHide: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE
) : OnFinishProcessComponent {

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        val isRemote = source !== target && target !== VirtualMachine.NULL_MACHINE
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()
        val hasSoftware = if (isRemote) {
            targetHDD.hasSoftware(softwareToHide.id())
        } else {
            sourceHDD.hasSoftware(softwareToHide.id())
        }
        if (hasSoftware && sourceHDD.hasSoftware(hiderSoftware.id()) && !softwareToHide.isRunning()) {
            softwareToHide.replace(VisibleComponent()) {
                hiddenVersion = hiderSoftware.component<VersionedComponent>().version
            }
            if(isRemote) {
                target.updateEvents.tryEmit(VirtualSoftwareUpdateEvent(target, targetHDD))
            } else {
                source.updateEvents.tryEmit(VirtualSoftwareUpdateEvent(target, sourceHDD))
            }
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
}