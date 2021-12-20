package com.server.engine.network.channel.login

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import io.netty.buffer.Unpooled

enum class LoginResponse {

    ACCEPTED,
    BANNED,
    INVALID,
    LOCKED,
    WORLD_FULL;

    companion object : PacketEncoder<LoginResponse> {
        override fun encode(message: LoginResponse): Packet {
            val content = Unpooled.buffer()
            content.writeByte(message.ordinal)
            return Packet(0, content)
        }
    }

}