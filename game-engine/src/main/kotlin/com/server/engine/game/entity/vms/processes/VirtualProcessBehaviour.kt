package com.server.engine.game.entity.vms.processes

interface VirtualProcessBehaviour {

    val threadCost: Int
    val runningTime: Long

    suspend fun onTick()

}