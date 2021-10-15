package com.server.engine.packets.login

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketCodec
import com.server.engine.packets.buf.readSimpleString
import io.netty.buffer.Unpooled

class LoginCodec : PacketCodec<LoginMessage, LoginResponse> {
    override fun decode(packet: Packet): LoginMessage {
        val (_, _, content) = packet
        val username = content.readSimpleString()
        val password = content.readSimpleString()
        return LoginMessage(username, password)
    }

    override fun encode(message: LoginResponse): Packet {
        val content = Unpooled.buffer()
        content.writeByte(message.ordinal)
        return Packet(0, 0, content)
    }
}