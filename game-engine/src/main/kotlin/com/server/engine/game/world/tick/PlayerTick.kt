package com.server.engine.game.world.tick

import com.server.engine.dispatchers.PlayerDispatcher
import com.server.engine.game.entity.character.player.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlayerTick {

    private val subscriptions = mutableListOf<Job>()

    val tick = flow {
        while(true) {
            emit(Unit)
            delay(500)
        }
    }

    fun subscribe(sub: TickSubscription) : Job {
        val job = tick.onEach { sub.onTick() }.launchIn(PlayerDispatcher)
        subscriptions.add(job)
        return job
    }

    fun subscribePlayer(player: Player) : Subscription<Player> {
        val job = tick.onEach {
            if(player.session.isActive) {
                player.onTick()
            } else {
                player.logout()
            }
        }.launchIn(PlayerDispatcher)
        val subscription = Subscription(player, job)
        player.subscription = subscription
        subscriptions.add(subscription)
        return subscription
    }

}