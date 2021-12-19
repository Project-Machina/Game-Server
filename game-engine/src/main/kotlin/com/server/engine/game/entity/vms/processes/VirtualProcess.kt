package com.server.engine.game.entity.vms.processes

import com.server.engine.utilities.get
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named

class VirtualProcess(
    val name: String,
    val immediate: Boolean = false,
    val behaviours: List<VirtualProcessBehaviour> = mutableListOf(),
    val onFinishBehaviour: VirtualProcessBehaviour = VirtualProcessBehaviour.NO_BEHAVIOUR
) {

    val minimalRunningTime: Long get() = behaviours.sumOf { it.runningTime }
    val threadCost: Int get() {
        return if(isPaused || isComplete) 0 else behaviours.sumOf { it.threadCost }
    }
    val ramCost: Long get() {
        val total = behaviours.sumOf { it.ramCost }
        return if(isPaused || isComplete) (total / 2) else total
    }
    val networkCost: Int get() {
        return if(isPaused || isComplete) 0 else behaviours.sumOf { it.networkCost }
    }

    val isComplete: Boolean get() {
        val runTime = if(preferredRunningTime <= minimalRunningTime) {
            minimalRunningTime
        } else {
            preferredRunningTime
        }
        return elapsedTime >= runTime
    }
    var preferredRunningTime: Long = minimalRunningTime
    var elapsedTime: Long = 0
    var pid: Int = -1
    var isPaused: Boolean = false
    var shouldComplete: Boolean = false
    var isKilled: Boolean = false

    fun toJson() : JsonObject {
        return buildJsonObject {
            put("name", name)
            put("immediate", immediate)
            put("paused", isPaused)
            put("behaviourKeys", buildJsonArray {
                for (behaviour in behaviours) {
                    add(behaviour::class.simpleName)
                }
                add(onFinishBehaviour::class.simpleName)
            })
        }
    }

    companion object {
        val NO_PROCESS = VirtualProcess("no_process")

        fun fromJson(obj: JsonObject) : VirtualProcess {
            val name = obj["name"]!!.jsonPrimitive.content
            val immediate = obj["immediate"]!!.jsonPrimitive.boolean
            val paused = obj["paused"]!!.jsonPrimitive.boolean
            val behaviourKeys = obj["behaviourKeys"]!!.jsonArray
            val behaviours = mutableListOf<VirtualProcessBehaviour>()
            for (behaviourKey in behaviourKeys) {
                val key = behaviourKey.jsonPrimitive.content
                val beh = get<VirtualProcessBehaviour>(named(key))
                behaviours.add(beh)
            }
            val onFinishBehaviour = behaviours.removeLast()
            val pc = VirtualProcess(name, immediate, behaviours, onFinishBehaviour)
            pc.isPaused = paused
            return pc
        }
    }
}