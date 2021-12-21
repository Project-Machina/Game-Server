package com.server.engine.packets.incoming

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.utilities.inject

class LogoutHandler(override val opcode: Int = 3, val player: Player) : PacketHandler<Unit, Unit> {

    private val loginSub: LoginSubscription by inject()

    override fun decode(packet: Packet, session: NetworkSession) {}

    override suspend fun handle(message: Unit) {
        loginSub.logoutQueue.add(player)
    }
}