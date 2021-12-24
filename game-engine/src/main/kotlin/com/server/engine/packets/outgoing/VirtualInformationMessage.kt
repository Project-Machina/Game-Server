package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.events.AlertType
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualInformationMessage(val title: String, val message: String, val type: AlertType = AlertType.INFORMATION) {
    companion object : PacketEncoder<VirtualInformationMessage> {
        override fun encode(message: VirtualInformationMessage): Packet {
            val content = Unpooled.buffer()
            content.writeSimpleString(message.title)
            content.writeSimpleString(message.message)
            content.writeSimpleString(message.type.name)
            return Packet(VIRTUAL_INFO, content)
        }
    }
}