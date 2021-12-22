package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import io.netty.buffer.Unpooled
import java.io.File

class NpcPageMessage(val name: String)  {
    companion object : PacketEncoder<NpcPageMessage> {
        override fun encode(message: NpcPageMessage): Packet {
            val file = File("/home/david/IdeaProjects/ServerGameEngine/world/assets/${message.name}.fxml")
            val buf = Unpooled.wrappedBuffer(file.readBytes())
            return Packet(NPC_FILE_DATA, buf)
        }
    }
}