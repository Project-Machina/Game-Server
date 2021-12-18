package com.server.engine.game.entity.vms.processes

class VirtualProcess(
    val name: String,
    val immediate: Boolean = false,
    val behaviours: List<VirtualProcessBehaviour> = mutableListOf(),
    val onFinishBehaviour: VirtualProcessBehaviour = VirtualProcessBehaviour.NO_BEHAVIOUR
) {

    val minimalRunningTime: Long get() = behaviours.sumOf { it.runningTime }
    val threadCost: Int get() {
        return if(isPaused) 0 else behaviours.sumOf { it.threadCost }
    }
    val isComplete: Boolean get() = elapsedTime >= preferredRunningTime
    var preferredRunningTime: Long = minimalRunningTime
    var elapsedTime: Long = 0
    var pid: Int = -1
    var isPaused: Boolean = false
    var shouldComplete: Boolean = false
    var isKilled: Boolean = false


    companion object {
        val NO_PROCESS = VirtualProcess("no_process")
    }
}