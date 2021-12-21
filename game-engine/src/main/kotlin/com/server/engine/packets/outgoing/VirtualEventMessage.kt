package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualEventMessage(val event: VirtualEvent) {
    companion object : PacketEncoder<VirtualEventMessage> {
        override fun encode(message: VirtualEventMessage): Packet {
            val buf = Unpooled.buffer()
            val event = message.event

            buf.writeInt(event.eventId)
            buf.writeLong(event.timestamp.time)
            buf.writeSimpleString(event.source)
            buf.writeSimpleString(event.message)

            return Packet(VIRTUAL_EVENT, buf)
        }
    }
}