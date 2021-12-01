package com.server.engine.game.world.tick.events

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.GameTick
import com.server.engine.game.world.tick.TickSubscription
import com.server.engine.utilities.inject

class LoginSubscription : TickSubscription {

    val world: GameWorld by inject()
    val tick: GameTick by inject()

    val loginQueue = ArrayDeque<Player>()
    val logoutQueue = ArrayDeque<Player>()

    override suspend fun onTick() {

        val logins = loginQueue.take(40)

        logins.forEach {
            val sub = tick.subscribePlayer(it)
            if (sub.isActive) {
                it.onLogin()
            }
        }

        loginQueue.removeAll(logins)

        val logouts = logoutQueue.take(40)

        logouts.forEach {
            it.logout()
        }

        logoutQueue.removeAll(logouts)

    }
}