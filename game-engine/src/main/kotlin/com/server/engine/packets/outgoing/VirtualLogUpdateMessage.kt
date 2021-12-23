package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.components.vevents.VirtualEvent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualLogUpdateMessage(val logs: List<VirtualEvent>, val isRemote: Boolean = false) {
    companion object : PacketEncoder<VirtualLogUpdateMessage> {
        override fun encode(message: VirtualLogUpdateMessage): Packet {
            val buf = Unpooled.buffer()

            buf.writeShort(message.logs.size)
            buf.writeBoolean(message.isRemote)

            for (log in message.logs) {

                buf.writeInt(log.eventId)
                buf.writeSimpleString(log.source)
                buf.writeSimpleString(log.message)
                buf.writeLong(log.timestamp)
                buf.writeBoolean(log.hiddenVersion > 0.0)

            }

            return Packet(VIRTUAL_LOG, buf)
        }
    }
}