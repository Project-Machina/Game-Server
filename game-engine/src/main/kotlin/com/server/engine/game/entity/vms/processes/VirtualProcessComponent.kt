package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.events.impl.VirtualProcessUpdateEvent
import com.server.engine.game.world.tick.GameTick
import kotlinx.serialization.json.*

class VirtualProcessComponent : VMComponent {

    private val _activeProcesses = mutableMapOf<Int, VirtualProcess>()
    val activeProcesses : Map<Int, VirtualProcess> = _activeProcesses
    val threadUsage: Int get() = activeProcesses.values.sumOf { it.threadCost }
    val ramUsage: Long get() = activeProcesses.values.sumOf { it.ramCost }
    val networkUsage: Int get() = activeProcesses.values.sumOf { it.networkCost }

    fun calculateRunningTime(defaultRunTime: Long = 3000L, threadCost: Int, threads: Int) : Long {
        val offset: Double = (threads.toDouble() / (threadUsage + threadCost))
        val extendedRunningTime = if(offset < 1) {
            (defaultRunTime / offset)
        } else defaultRunTime
        return extendedRunningTime.toLong()
    }

    fun addProcess(process: VirtualProcess) {
        val pid = getProcessId()
        if (pid > 0) {
            process.pid = pid
            _activeProcesses[pid] = process
        }
    }

    private fun getProcessId() : Int {
        var pid = 0
        var attempts = 0
        do {
            if(attempts >= 2)
                break
            if(pid > 255) {
                attempts++
                pid = 0
            }
            pid++
        } while(activeProcesses.containsKey(pid))
        return pid
    }

    override suspend fun onTick(source: VirtualMachine) {
        val iter = _activeProcesses.iterator()
        val mb = source.component<MotherboardComponent>()
        while(iter.hasNext()) {
            val set = iter.next()
            val pc = set.value

            if(pc.isKilled) {
                iter.remove()
                source.updateEvents.emit(VirtualProcessUpdateEvent(source, pc))
                continue
            }

            if(pc.immediate) {
                pc.behaviours.forEach { it.onTick(source, pc) }
                pc.onFinishBehaviour.onTick(source, pc)
                iter.remove()
            } else {

                if (!pc.isPaused && !pc.isComplete) {
                    if (!pc.isIndeterminate) {
                        pc.elapsedTime += GameTick.GAME_TICK_MILLIS
                        pc.preferredRunningTime = calculateRunningTime(
                            pc.minimalRunningTime,
                            pc.threadCost,
                            mb.availableThreads
                        )
                    }
                    pc.behaviours.forEach { it.onTick(source, pc) }
                }
                if(pc.isComplete && pc.shouldComplete) {
                    pc.onFinishBehaviour.onTick(source, pc)
                    iter.remove()
                }
                source.updateEvents.emit(VirtualProcessUpdateEvent(source, pc))
            }
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("processes", buildJsonArray {
                activeProcesses.values.forEach {
                    add(it.toJson())
                }
            })
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("processes")) {
            val processArray = json["processes"]!!.jsonArray
            processArray.forEach {
                addProcess(VirtualProcess.fromJson(it.jsonObject))
            }
        }
    }
}