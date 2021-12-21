package com.server.engine.game.world.tick

import com.server.engine.dispatchers.PlayerDispatcher
import com.server.engine.dispatchers.VirtualMachineDispatcher
import com.server.engine.game.entity.character.player.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VirtualMachineTick {

    private val subscriptions = mutableListOf<Job>()

    val tick = flow {
        while(true) {
            emit(Unit)
            delay(GAME_TICK_MILLIS)
        }
    }

    fun subscribe(sub: TickSubscription) : Job {
        val job = tick.onEach { sub.onTick() }.launchIn(VirtualMachineDispatcher)
        subscriptions.add(job)
        return job
    }

    companion object {

        const val GAME_TICK_MILLIS = 300L

    }
}