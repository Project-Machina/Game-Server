package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualInformationMessage(val title: String, val message: String) {
    companion object : PacketEncoder<VirtualInformationMessage> {
        override fun encode(message: VirtualInformationMessage): Packet {
            val content = Unpooled.buffer()
            content.writeSimpleString(message.title)
            content.writeSimpleString(message.message)
            return Packet(VIRTUAL_ERROR, content)
        }
    }
}