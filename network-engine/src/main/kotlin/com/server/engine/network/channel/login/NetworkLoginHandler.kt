package com.server.engine.network.channel.login

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.session.NetworkSession

interface NetworkLoginHandler {

    suspend fun handle(message: LoginMessage) : LoginResponse

    fun decode(packet: Packet, session: NetworkSession) : LoginMessage

}