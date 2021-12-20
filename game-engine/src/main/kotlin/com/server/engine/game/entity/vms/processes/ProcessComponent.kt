package com.server.engine.game.entity.vms.processes

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.utilities.int
import com.server.engine.utilities.long
import kotlinx.serialization.json.*

interface ProcessComponent : Component {

    var threadCost: Int
    var networkCost: Int
    var ramCost: Long
    var runningTime: Long

    suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {}

    override fun save(): JsonObject {
        return buildJsonObject {
            put("threadCost", threadCost)
            put("networkCost", networkCost)
            put("ramCost", ramCost)
            put("runningTime", runningTime)
        }
    }
    override fun load(json : JsonObject) {
        threadCost = json.int("threadCost")
        networkCost = json.int("networkCost")
        ramCost = json.long("ramCost")
        runningTime = json.long("runningTime")
    }

    companion object {

        val NO_BEHAVIOUR = createAnonymous { _, _ -> }

        fun createAnonymous(runningTime: Long = 3000L, threadCost: Int = 1, ramUsage: Long = 1, networkCost: Int = 0, onTick: suspend (VirtualMachine, VirtualProcess) -> Unit) : ProcessComponent {
            return object : ProcessComponent {
                override var networkCost: Int = networkCost
                override var ramCost: Long = ramUsage
                override var threadCost: Int = threadCost
                override var runningTime: Long = runningTime
                override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
                    onTick(source, process)
                }
            }
        }

    }

}