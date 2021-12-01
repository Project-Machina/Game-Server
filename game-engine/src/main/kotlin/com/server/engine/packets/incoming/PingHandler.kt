package com.server.engine.packets.incoming

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.utilities.readSimpleString

class PingHandler(override val opcode: Int = 1) : PacketHandler<String, Unit> {
    override fun decode(packet: Packet, session: NetworkSession): String {
        return packet.content.readSimpleString()
    }

    override fun handle(message: String) {
        println(message)
    }
}