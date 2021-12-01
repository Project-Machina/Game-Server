package com.server.engine.packets.login

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.network.channel.login.LoginMessage
import com.server.engine.network.channel.login.LoginResponse
import com.server.engine.network.channel.login.NetworkLoginHandler
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.utilities.readSimpleString
import com.server.engine.utilities.inject

class LoginHandler(override val opcode: Int = 0) : PacketHandler<LoginMessage, LoginResponse>, NetworkLoginHandler {

    private val loginSub: LoginSubscription by inject()

    override fun handle(message: LoginMessage): LoginResponse {

        if(loginAttempts.containsKey(message.username) && loginAttempts[message.username]!! >= 3)
            return LoginResponse.LOCKED

        //TODO - validate user through database

        println("Player ${message.username} - logging in.")

        if(message.username.lowercase() == "javatar") {

            loginSub.loginQueue.add(Player(message.username, message.session))

            return LoginResponse.ACCEPTED
        }

        val attempts = loginAttempts.getOrPut(message.username) { 0 }
        loginAttempts[message.username] = attempts + 1
        return LoginResponse.INVALID
    }

    override fun decode(packet: Packet, session: NetworkSession): LoginMessage {
        val (_, _, content) = packet
        return LoginMessage(content.readSimpleString(), content.readSimpleString(), session)
    }

    companion object {
        val loginAttempts = mutableMapOf<String, Int>()
    }
}