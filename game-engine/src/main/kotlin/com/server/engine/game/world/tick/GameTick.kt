package com.server.engine.game.world.tick

import com.server.engine.dispatchers.GameDispatcher
import com.server.engine.game.entity.character.player.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class GameTick {

    private val subscriptions = mutableListOf<Job>()

    val tick = flow {
        while(true) {
            emit(Unit)
            delay(200)
        }
    }

    fun subscribe(sub: TickSubscription) : Job {
        val job = tick.onEach { sub.onTick() }.launchIn(GameDispatcher)
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
        }.launchIn(GameDispatcher)
        val subscription = Subscription(player, job)
        player.subscription = subscription
        subscriptions.add(subscription)
        return subscription
    }
}