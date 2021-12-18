package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.impl.VirtualProcessUpdateEvent
import kotlinx.coroutines.delay
import kotlin.random.Random

class VirtualProcessComponent(val source: VirtualMachine) : VMComponent {

    private val _activeProcesses = mutableMapOf<Int, VirtualProcess>()
    val activeProcesses : Map<Int, VirtualProcess> = _activeProcesses

    val threads: Int = 2
    val threadUsage: Int get() = activeProcesses.values.sumOf { it.threadCost }

    fun calculateRunningTime(defaultRunTime: Long = 3000L, threadCost: Int) : Long {
        val offset: Double = (threads.toDouble() / (threadUsage + threadCost))
        val extendedRunningTime = if(offset < 1) {
            (defaultRunTime / offset)
        } else defaultRunTime
        return extendedRunningTime.toLong()
    }

    fun addProcess(process: VirtualProcess) {
        val pid = getProcessId()
        process.pid = pid
        _activeProcesses[pid] = process
    }

    private fun getProcessId() : Int {
        var pid: Int
        do {
            pid = Random.nextInt()
        } while(activeProcesses.containsKey(pid))
        return pid
    }

    override suspend fun onTick() {
        val iter = _activeProcesses.iterator()
        while(iter.hasNext()) {
            val set = iter.next()
            val pc = set.value
            if(pc.immediate) {
                pc.behaviours.forEach { it.onTick() }
                pc.onFinishBehaviour.onTick()
                iter.remove()
            } else {
                pc.elapsedTime += 1000
                pc.preferredRunningTime = calculateRunningTime(pc.minimalRunningTime, pc.threadCost)
                pc.behaviours.forEach { it.onTick() }
                if(pc.elapsedTime >= pc.preferredRunningTime) {
                    pc.onFinishBehaviour.onTick()
                    iter.remove()
                }
            }
        }
        source.updateEvents.emit(VirtualProcessUpdateEvent(source, this))
        delay(1000)
    }
}