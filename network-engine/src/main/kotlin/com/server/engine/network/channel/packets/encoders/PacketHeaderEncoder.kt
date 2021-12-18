package com.server.engine.network.channel.packets.encoders

import com.server.engine.network.channel.packets.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketHeaderEncoder : MessageToByteEncoder<Packet>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        out.writeInt(msg.content.readableBytes() + 2)
        out.writeShort(msg.opcode)
        out.writeBytes(msg.content)
    }
}