package com.server.engine.game.entity.character.player

import com.server.engine.game.entity.character.Character
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.world.tick.Subscription
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.incoming.PingHandler
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.CancellationException

class Player(val name: String, val session: NetworkSession) : Character() {

    private val _subscription = MutableStateFlow<Subscription<Player>?>(null)
    override var subscription: Subscription<Player>? by _subscription

    fun onLogin() {
        with(VirtualMachineLinkComponent())

        session.handlePacket(PingHandler())
    }

    fun logout() {
        if (subscription != null) {
            subscription?.cancel(CancellationException("Logging out player $name"))
        }
    }

    override suspend fun onTick() {
    }

    override fun isActive(): Boolean {
        return subscription?.isActive ?: false
    }
}
