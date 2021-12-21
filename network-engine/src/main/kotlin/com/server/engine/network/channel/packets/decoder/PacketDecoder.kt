package com.server.engine.network.channel.packets.decoder

import com.server.engine.network.channel.packets.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder

class PacketDecoder : MessageToMessageDecoder<ByteBuf>() {
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf?, out: MutableList<Any>) {
        if (msg != null) {
            val opcode = msg.readUnsignedShort()
            val frame = msg.copy()
            println("Decoding Packet $opcode")
            out.add(Packet(opcode, frame))
        }
    }
}