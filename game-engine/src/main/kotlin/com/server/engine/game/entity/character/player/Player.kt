package com.server.engine.game.entity.character.player

import com.server.engine.game.entity.character.Character
import com.server.engine.game.entity.character.components.RankComponent
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.world.tick.Subscription
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.incoming.LogoutHandler
import com.server.engine.packets.incoming.PingHandler
import com.server.engine.packets.incoming.VmCommandHandler
import com.server.engine.packets.outgoing.PlayerStatistics
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.CancellationException

class Player(val name: String, val session: NetworkSession) : Character() {

    private val loginSub: LoginSubscription by inject()

    private val _subscription = MutableStateFlow<Subscription<Player>?>(null)
    override var subscription: Subscription<Player>? by _subscription

    fun onLogin() {
        with(VirtualMachineLinkComponent())
        with(RankComponent())

        if (name.lowercase() == "javatar") {
            val link = component<VirtualMachineLinkComponent>()
            link.linkTo("74.97.118.97")
        }

        session.handlePacket(PingHandler())
        session.handlePacket(VmCommandHandler(player = this))
        session.handlePacket(LogoutHandler(player = this))
    }

    fun logout() {
        if (subscription != null) {
            subscription?.cancel(CancellationException("Logging out player $name"))
            println("Logging out player $name.")
        }
    }

    override suspend fun onTick() {
        session.sendMessage(PlayerStatistics(this))
    }

    override fun isActive(): Boolean {
        return subscription?.isActive ?: false
    }
}
