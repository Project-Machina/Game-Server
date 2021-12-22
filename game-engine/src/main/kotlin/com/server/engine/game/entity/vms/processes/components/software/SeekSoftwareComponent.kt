package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.replace
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.entity.vms.software.isRunning
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.jobj
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class SeekSoftwareComponent(
    override var threadCost: Int,
    var target: VirtualMachine = VirtualMachine.NULL_MACHINE,
    var seekerSoftware: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    var softwareToSeek: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE
) : OnFinishProcessComponent {
    override var networkCost: Int = 0
    override var ramCost: Long = 0
    override var runningTime: Long = 3000

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        val isRemote = source !== target && target !== VirtualMachine.NULL_MACHINE
        val sourceHDD = source.component<HardDriveComponent>()
        val targetHDD = target.component<HardDriveComponent>()
        val hasSoftware = if (isRemote) {
            targetHDD.hasSoftware(softwareToSeek.id())
        } else {
            sourceHDD.hasSoftware(softwareToSeek.id())
        }
        if (hasSoftware && sourceHDD.hasSoftware(seekerSoftware.id()) && !softwareToSeek.isRunning()) {
            softwareToSeek.replace(VisibleComponent())
            if(isRemote) {
                target.systemOutput.tryEmit(SystemSoftwareAlert(target, targetHDD))
            } else {
                source.systemOutput.tryEmit(SystemSoftwareAlert(target, sourceHDD))
            }
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            put("target", target.id.toString())
            put("seekerSoftware", seekerSoftware.saveComponents())
            put("softwareToSeek", softwareToSeek.saveComponents())
        }
    }

    override fun load(json: JsonObject) {
        super.load(json["stats"]!!.jsonObject)
        val target = GameWorld.vmachine(json.string("target"))
        val seekerSoftware = SoftwareBuilder.software()
        val softwareToSeek = SoftwareBuilder.software()
        seekerSoftware.loadComponents(json.jobj("seekerSoftware"))
        softwareToSeek.loadComponents(json.jobj("softwareToSeek"))
        if (seekerSoftware.name.isNotEmpty()) {
            this.seekerSoftware = seekerSoftware
        }
        if (softwareToSeek.name.isNotEmpty()) {
            this.softwareToSeek = softwareToSeek
        }
        if (target != null) {
            this.target = target
        }
    }

    companion object : ComponentFactory<SeekSoftwareComponent> {
        override fun create(): SeekSoftwareComponent {
            return SeekSoftwareComponent(0)
        }
    }
}