package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.NULL_MACHINE
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.ProcessComponent
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
        val pcm: VirtualProcessComponent = if(source !== target && target !== NULL_MACHINE) {
            target.component()
        } else source.component()

        val mb: MotherboardComponent = if(source !== target && target !== NULL_MACHINE) {
            target.component()
        } else source.component()

        val pc = if(remSoftware === VirtualSoftware.NULL_SOFTWARE) {
            VirtualProcess(software.fullName, isIndeterminate = true)
        } else VirtualProcess("${software.fullName} (Encrypted)", isIndeterminate = true)

        var ramCost = (software.size * 0.01).toLong()
        if(ramCost <= 0) {
           ramCost = 1
        }

        pc.singleton<ResourceComponent>(ResourceUsageComponent(ramCost = ramCost))
        pc.with(SoftwareLinkComponent(software))

        val requiredRAM = pc.ramCost + pcm.ramUsage

        if(requiredRAM >= mb.availableRam) {
            return
        }

        software.replace(ProcessOwnerComponent()) {
            pid = pcm.addProcess(pc)
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

    override fun load(obj: JsonObject) {
        super.load(obj["stats"]!!.jsonObject)
        if(obj.containsKey("target")) {
            val target = vmachine(obj["target"]!!.jsonPrimitive.content)
            if (target != null) {
                this.target = target
            }
        }
        if(obj.containsKey("software")) {
            software = fromJson(obj["software"]!!.jsonObject)
        }
        if(obj.containsKey("remSoftware")) {
            remSoftware = fromJson(obj["remSoftware"]!!.jsonObject)
        }
    }
}