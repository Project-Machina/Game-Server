package com.server.engine.game.entity.vms.processes.components.logs

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.version
import com.server.engine.game.world.GameWorld.Companion.vmachine
import com.server.engine.utilities.int
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class HideLogComponent(
    override var threadCost: Int = 0,
    override var networkCost: Int = 0,
    override var ramCost: Long = 0,
    override var runningTime: Long = 0,
    private var hiderSoft: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    private var logId: Int = -1,
    private var target: VirtualMachine = VirtualMachine.NULL_MACHINE
) : OnFinishProcessComponent {

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        if(hiderSoft === VirtualSoftware.NULL_SOFTWARE || logId == -1 || target == VirtualMachine.NULL_MACHINE)
            return
        val sourceHDD = source.component<HardDriveComponent>()
        if(sourceHDD.hasSoftware(hiderSoft.id())) {
            val targetLogs = target.component<SystemLogsComponent>()
            if(logId in targetLogs) {
                targetLogs.hideLog(logId, hiderSoft.version)
            }
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            put("hiderSoft", hiderSoft.saveComponents())
            put("logId", logId)
            put("target", target.id.toString())
        }
    }

    override fun load(json: JsonObject) {
        super.load(json["stats"]!!.jsonObject)
        val hiderSoft = software()
        hiderSoft.loadComponents(json["hiderSoft"]!!.jsonObject)
        this.logId = json.int("logId")
        this.hiderSoft = hiderSoft
        val target = vmachine(json.string("target"))
        if(target != null) {
            this.target = target
        }
    }

    companion object : ComponentFactory<HideLogComponent> {
        override fun create(): HideLogComponent {
            return HideLogComponent()
        }
    }
}