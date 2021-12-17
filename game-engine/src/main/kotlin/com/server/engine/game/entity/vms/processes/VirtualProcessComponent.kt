package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.impl.VirtualProcessUpdateEvent
import kotlinx.coroutines.delay

class VirtualProcessComponent(val source: VirtualMachine) : VMComponent {

    val activeProcesses = mutableListOf<VirtualProcess>()

    val threads: Int = 2
    val threadUsage: Int get() = activeProcesses.sumOf { it.threadCost }

    fun calculateRunningTime(defaultRunTime: Long = 3000L, threadCost: Int) : Long {
        val offset: Double = (threads.toDouble() / (threadUsage + threadCost))
        val extendedRunningTime = if(offset < 1) {
            (defaultRunTime / offset)
        } else defaultRunTime
        return extendedRunningTime.toLong()
    }

    override suspend fun onTick() {
        val iter = activeProcesses.iterator()
        while(iter.hasNext()) {
            val pc = iter.next()
            if(pc.immediate) {
                pc.behaviours.onEach { it.onTick() }
                iter.remove()
            } else {
                pc.elapsedTime += 1000
                pc.preferredRunningTime = calculateRunningTime(pc.minimalRunningTime, pc.threadCost)
                source.updateEvents.tryEmit(VirtualProcessUpdateEvent(source, this))
                if(pc.elapsedTime >= pc.preferredRunningTime) {
                    iter.remove()
                }
            }
        }
        delay(1000)
    }
}