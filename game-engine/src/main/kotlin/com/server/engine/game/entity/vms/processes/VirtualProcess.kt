package com.server.engine.game.entity.vms.processes

class VirtualProcess(
    val name: String,
    val immediate: Boolean = false,
    val behaviours: List<VirtualProcessBehaviour> = mutableListOf()
) {

    var elapsedTime: Long = 0

    val minimalRunningTime: Long get() = behaviours.sumOf { it.runningTime }
    val threadCost: Int get() = behaviours.sumOf { it.threadCost }

    var preferredRunningTime: Long = minimalRunningTime

}