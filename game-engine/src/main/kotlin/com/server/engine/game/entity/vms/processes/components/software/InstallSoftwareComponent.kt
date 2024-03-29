package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.NULL_MACHINE
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.events.impl.SystemAlert
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.fireSoftwareChange
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.with
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.ResourceComponent
import com.server.engine.game.entity.vms.processes.components.ResourceUsageComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.fromJson
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.replace
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.world.GameWorld.Companion.vmachine
import kotlinx.serialization.json.*

class InstallSoftwareComponent(
    override var threadCost: Int = 1,
    override var networkCost: Int = 0,
    override var ramCost: Long = 0,
    override var runningTime: Long = 3000,
    private var software: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    private var remSoftware: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    private var target: VirtualMachine = NULL_MACHINE,
) : OnFinishProcessComponent {

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        if (target !== source) {
            val sourceConn = source.component<ConnectionComponent>()
            if(!sourceConn.isConnectedTo(target.address)) {
                source.systemOutput.emit(SystemAlert(
                    "Connection lost... please reestablish",
                    source,
                    "Software Install",
                ))
                return
            }
        }

        val hdd: HardDriveComponent = target.component()
        if (!hdd.hasSoftware(software.id()))
            return
        val pcm: VirtualProcessComponent = target.component()
        val mb: MotherboardComponent = target.component()

        val pc = if (remSoftware === VirtualSoftware.NULL_SOFTWARE) {
            VirtualProcess(software.fullName, isIndeterminate = true)
        } else VirtualProcess("${software.fullName} (Encrypted)", isIndeterminate = true)

        var ramCost = (software.size * 0.01).toLong()
        if (ramCost <= 0) {
            ramCost = 1
        }
        pc.singleton<ResourceComponent>(ResourceUsageComponent(ramCost = ramCost))
        pc.with(SoftwareLinkComponent(software))
        val requiredRAM = pc.ramCost + pcm.ramUsage
        if (requiredRAM >= mb.availableRam) {
            source.systemOutput.emit(SystemAlert("Not enough RAM available.", source))
            return
        }
        software.replace(ProcessOwnerComponent()) {
            pid = pcm.addProcess(pc)
        }
        target.systemOutput.emit(SystemSoftwareAlert(target, hdd))

        if(target.has<ConnectionComponent>()) {
            val vm = target.component<ConnectionComponent>().remoteVM
            vm?.fireSoftwareChange()
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            if (target !== NULL_MACHINE) {
                put("target", target.id.toString())
            }
            if (software !== VirtualSoftware.NULL_SOFTWARE) {
                put("software", software.saveComponents())
            }
            if (remSoftware !== VirtualSoftware.NULL_SOFTWARE) {
                put("remSoftware", remSoftware.saveComponents())
            }
        }
    }

    override fun load(json: JsonObject) {
        super.load(json["stats"]!!.jsonObject)
        if (json.containsKey("target")) {
            val target = vmachine(json["target"]!!.jsonPrimitive.content)
            if (target != null) {
                this.target = target
            }
        }
        if (json.containsKey("software")) {
            software = fromJson(json["software"]!!.jsonObject)
        }
        if (json.containsKey("remSoftware")) {
            remSoftware = fromJson(json["remSoftware"]!!.jsonObject)
        }
    }

    companion object : ComponentFactory<InstallSoftwareComponent> {
        override fun create(): InstallSoftwareComponent {
            return InstallSoftwareComponent()
        }
    }
}