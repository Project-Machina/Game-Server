package com.server.engine.packets.outgoing

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class ParameterMessage(val params: Map<String, Any?>, val clear: Boolean = false) {
    companion object : PacketEncoder<ParameterMessage> {
        override fun encode(message: ParameterMessage): Packet {
            val buf = Unpooled.buffer()
            buf.writeBoolean(message.clear)
            if(!message.clear) {
                buf.writeShort(message.params.size)
                message.params.forEach { (key, msg) ->
                    buf.writeSimpleString(key)
                    buf.writeBoolean(msg == null)
                    if (msg != null) {
                        when(msg) {
                            is Boolean -> {
                                buf.writeByte(1)
                                buf.writeBoolean(msg)
                            }
                            is String -> {
                                buf.writeByte(2)
                                buf.writeSimpleString(msg)
                            }
                            is Int -> {
                                buf.writeByte(3)
                                buf.writeInt(msg)
                            }
                        }
                    }
                }
            }
            return Packet(PARAMETER_MESSAGE, buf)
        }
    }
}