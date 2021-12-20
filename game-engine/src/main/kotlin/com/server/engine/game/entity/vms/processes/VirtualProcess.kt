package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.processes.behaviours.BehaviourFactory
import com.server.engine.utilities.get
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named

class VirtualProcess(
    val name: String,
    val immediate: Boolean = false,
    val isIndeterminate: Boolean = false,
    val behaviours: List<VirtualProcessBehaviour> = mutableListOf(),
    val onFinishBehaviour: VirtualProcessBehaviour = VirtualProcessBehaviour.NO_BEHAVIOUR,
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
            put("isIndeterminate", isIndeterminate)
            put("behaviourKeys", buildJsonArray {
                for (behaviour in behaviours) {
                    add(buildJsonObject {
                        put("key", behaviour::class.java.simpleName)
                        put("attributes", behaviour.save())
                    })
                }
                if(onFinishBehaviour !== VirtualProcessBehaviour.NO_BEHAVIOUR) {
                    add(buildJsonObject {
                        put("key", onFinishBehaviour::class.simpleName)
                        put("attributes", onFinishBehaviour.save())
                    })
                }
            })
        }
    }

    companion object {
        val NO_PROCESS = VirtualProcess("no_process")

        fun fromJson(obj: JsonObject) : VirtualProcess {
            val name = obj["name"]!!.jsonPrimitive.content
            val immediate = obj["immediate"]!!.jsonPrimitive.boolean
            val paused = obj["paused"]!!.jsonPrimitive.boolean
            val isIndeterminate = obj["isIndeterminate"]!!.jsonPrimitive.boolean
            val behaviours = mutableListOf<VirtualProcessBehaviour>()
            val behaviourKeys = obj["behaviourKeys"]!!.jsonArray
            for (behaviourKey in behaviourKeys) {
                val behObj = behaviourKey.jsonObject
                val key = behObj["key"]!!.jsonPrimitive.content
                val behFactory: BehaviourFactory<*> = get(named(key))
                val beh = behFactory.create()
                val attObj = behObj["attributes"]!!.jsonObject
                beh.load(attObj)
                behaviours.add(beh)
            }
            val onFinishBehaviour = behaviours.removeLast()
            val pc = VirtualProcess(name, immediate, isIndeterminate, behaviours, onFinishBehaviour)
            pc.isPaused = paused
            return pc
        }
    }
}