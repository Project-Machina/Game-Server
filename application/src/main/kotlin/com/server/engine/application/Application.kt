package com.server.engine.application

import com.server.engine.game.*
import com.server.engine.game.world.tick.GameTick
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.network.NetworkServer
import com.server.engine.packets.outgoingPacketModule
import com.server.engine.utilities.get
import org.koin.core.context.startKoin

object Application {

    @JvmStatic
    fun main(args: Array<String>) {

        startKoin {
            modules(
                outgoingPacketModule,
                koinModule,
                subscriptionModule,
                softCompsModule,
                vmCompsModule
            )
        }

        val net = NetworkServer()

        val loginSub: LoginSubscription = get()
        val tick: GameTick = get()

        tick.subscribe(loginSub)

        net.start(43595)

    }

}