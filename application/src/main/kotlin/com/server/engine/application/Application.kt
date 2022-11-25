package com.server.engine.application

import com.server.engine.game.*
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.PlayerTick
import com.server.engine.game.world.tick.VirtualMachineTick
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.game.world.tick.events.WorldTickSubscription
import com.server.engine.network.NetworkServer
import com.server.engine.packets.outgoingPacketModule
import com.server.engine.utilities.get
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

object Application {

    @JvmStatic
    fun main(args: Array<String>) {

        startKoin {
            modules(
                outgoingPacketModule,
                etcModule,
                subscriptionModule,
                softCompsModule,
                vmCompsModule,
                processCompsModule
            )
        }

        val net = NetworkServer()
        val world = get<GameWorld>()
        val worldTick = get<WorldTickSubscription>()

        val loginSub: LoginSubscription = get()
        val playerTick: PlayerTick = get()
        val machineTick: VirtualMachineTick = get()


        world.loadTestWorld()
        playerTick.subscribe(loginSub)
        machineTick.subscribe(worldTick)

        net.start(43595)
    }

}