package com.server.engine.game.login

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.inject
import com.server.engine.game.koinModule
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.InternetProtocolManager
import com.server.engine.game.world.tick.GameTick
import com.server.engine.game.world.tick.events.LoginSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Ticking {

    @Test
    fun `login tick`() {

        startKoin {
            modules(module {
                single { InternetProtocolManager() }
                single { GameTick(Dispatchers.Unconfined) }
                single { GameWorld() }
            })
        }


        val player = Player("Javatar")
        val tick: GameTick by inject()
        val loginSub = LoginSubscription()

        tick.subscribe(loginSub)

        loginSub.loginQueue.add(player)

        while(true);

    }

}