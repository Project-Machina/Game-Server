package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualInformationMessage(val title: String, val message: String, val critical: Boolean = false) {
    companion object : PacketEncoder<VirtualInformationMessage> {
        override fun encode(message: VirtualInformationMessage): Packet {
            val content = Unpooled.buffer()
            content.writeSimpleString(message.title)
            content.writeSimpleString(message.message)
            content.writeBoolean(message.critical)
            return Packet(VIRTUAL_INFO, content)
        }
    }
}