package com.server.engine.game.world.tick

import com.server.engine.game.entity.character.player.Player
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class GameTick(val dispatcher: CoroutineDispatcher = tickDispatcher) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher

    private val subscriptions = mutableListOf<Job>()

    val tick = flow {
        while(true) {
            emit(Unit)
            delay(200)
        }
    }

    fun subscribe(sub: TickSubscription) : Job {
        val job = tick.onEach { sub.onTick() }.launchIn(this)
        subscriptions.add(job)
        return job
    }

    fun subscribePlayer(player: Player) : Subscription<Player> {
        val job = tick.onEach { player.onTick() }.launchIn(this)
        val subscription = Subscription(player, job)
        player.subscription = subscription
        subscriptions.add(subscription)
        return subscription
    }

    fun shutdown() {
        this.cancel()
    }

    companion object {
        val tickDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

}