package com.server.engine.game.entity.vms.processes

interface VirtualProcessBehaviour {

    val threadCost: Int
    val networkCost: Int
    val ramCost: Long
    val runningTime: Long

    suspend fun onTick()

    companion object {

        val NO_BEHAVIOUR = createAnonymous {}

        fun createAnonymous(runningTime: Long = 3000L, threadCost: Int = 1, ramUsage: Long = 1, networkCost: Int = 0, onTick: suspend () -> Unit) : VirtualProcessBehaviour {
            return object : VirtualProcessBehaviour {
                override val networkCost: Int = 0
                override val ramCost: Long = ramUsage
                override val threadCost: Int = threadCost
                override val runningTime: Long = runningTime
                override suspend fun onTick() {
                    onTick()
                }
            }
        }

    }

}