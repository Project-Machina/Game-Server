package com.server.engine.game.entity.vms.processes

interface VirtualProcessBehaviour {

    val threadCost: Int
    val runningTime: Long

    suspend fun onTick()

    companion object {

        fun createAnonymous(runningTime: Long = 3000L, threadCost: Int = 1, onTick: suspend () -> Unit) : VirtualProcessBehaviour {
            return object : VirtualProcessBehaviour {
                override val runningTime: Long = runningTime
                override val threadCost: Int = threadCost
                override suspend fun onTick() {
                    onTick()
                }
            }
        }

    }

}