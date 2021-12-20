package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.VirtualMachine
import kotlinx.serialization.json.*

interface VirtualProcessBehaviour {

    var threadCost: Int
    var networkCost: Int
    var ramCost: Long
    var runningTime: Long

    suspend fun onTick(source: VirtualMachine, process: VirtualProcess)

    fun save(): JsonObject {
        return buildJsonObject {
            put("threadCost", threadCost)
            put("networkCost", networkCost)
            put("ramCost", ramCost)
            put("runningTime", runningTime)
        }
    }
    fun load(obj : JsonObject) {
        threadCost = obj["threadCost"]!!.jsonPrimitive.int
        networkCost = obj["networkCost"]!!.jsonPrimitive.int
        ramCost = obj["ramCost"]!!.jsonPrimitive.long
        runningTime = obj["runningTime"]!!.jsonPrimitive.long
    }

    companion object {

        val NO_BEHAVIOUR = createAnonymous { _, _ -> }

        fun createAnonymous(runningTime: Long = 3000L, threadCost: Int = 1, ramUsage: Long = 1, networkCost: Int = 0, onTick: suspend (VirtualMachine, VirtualProcess) -> Unit) : VirtualProcessBehaviour {
            return object : VirtualProcessBehaviour {
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