package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled
import java.io.File

class NpcPageMessage(val useDefault: Boolean, val path: String)  {
    companion object : PacketEncoder<NpcPageMessage> {
        override fun encode(message: NpcPageMessage): Packet {
            val buffer = Unpooled.buffer()
            buffer.writeBoolean(message.useDefault)
            if (!message.useDefault) {
                buffer.writeSimpleString(message.path, true)
            }
            return Packet(NPC_FILE_DATA, buffer)
        }
    }
}