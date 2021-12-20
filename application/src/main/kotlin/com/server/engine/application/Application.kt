package com.server.engine.application

import com.server.engine.game.*
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.GameTick
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.game.world.tick.events.WorldTick
import com.server.engine.network.NetworkServer
import com.server.engine.network.session.NetworkSession.Companion.toPacket
import com.server.engine.packets.outgoing.VmCommandOutput
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
                vmCompsModule,
                processBehaviourModule
            )
        }

        val net = NetworkServer()
        val world = get<GameWorld>()
        val worldTick = get<WorldTick>()

        val loginSub: LoginSubscription = get()
        val tick: GameTick = get()

        world.loadTestWorld()
        tick.subscribe(loginSub)
        tick.subscribe(worldTick)

        net.start(43595)

    }

}