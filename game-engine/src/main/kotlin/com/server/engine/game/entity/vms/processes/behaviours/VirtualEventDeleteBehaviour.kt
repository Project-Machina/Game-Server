package com.server.engine.game.entity.vms.processes.behaviours

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.NULL_MACHINE
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour
import com.server.engine.game.world.GameWorld.Companion.vmachine
import kotlinx.serialization.json.*

class VirtualEventDeleteBehaviour(
    val eventIds: MutableList<Int> = mutableListOf(),
    target: VirtualMachine = NULL_MACHINE,
    override var threadCost: Int = 1
) : VirtualProcessBehaviour {

    override var runningTime: Long = 1000
    override var networkCost: Int = 0
    override var ramCost: Long = 0

    var target: VirtualMachine = target
        private set

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {

        if(source !== target && target !== NULL_MACHINE) {
            val vevents = target.component<VirtualEventsComponent>()
            eventIds.forEach { vevents.remove(it) }
        } else {
            val vevents = source.component<VirtualEventsComponent>()
            eventIds.forEach { vevents.remove(it) }
        }

    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("stats", super.save())
            put("target", target.id.toString())
            putJsonArray("eventIds") {
                eventIds.forEach { add(it) }
            }
        }
    }

    override fun load(obj: JsonObject) {
        super.load(obj["stats"]!!.jsonObject)
        val ids = obj["eventIds"]!!.jsonArray.map { it.jsonPrimitive.int }
        eventIds.addAll(ids)
        val target = vmachine(obj["target"]!!.jsonPrimitive.content)
        if(target != null)
            this.target = target
    }

    companion object : BehaviourFactory<VirtualEventDeleteBehaviour> {
        override fun create(): VirtualEventDeleteBehaviour {
            return VirtualEventDeleteBehaviour()
        }
    }
}