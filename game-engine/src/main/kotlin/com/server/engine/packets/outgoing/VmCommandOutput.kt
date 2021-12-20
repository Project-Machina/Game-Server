package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VmCommandOutput(val output: String, val remote: Boolean) {
    companion object : PacketEncoder<VmCommandOutput> {
        override fun encode(message: VmCommandOutput): Packet {
            val content = Unpooled.buffer()

            content.writeSimpleString(message.output)
            content.writeBoolean(message.remote)

            return Packet(1, content)
        }
    }
}