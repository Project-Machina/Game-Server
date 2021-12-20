package com.server.engine.network.channel.packets.codec

import com.server.engine.network.channel.packets.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

class PacketCodec : ByteToMessageCodec<Packet>() {
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= 4) {
            val opcode = buf.readUnsignedShort()
            val frameLength = buf.readUnsignedShort()
            println("$opcode - $frameLength")
            out.add(Packet(opcode, buf.readBytes(frameLength)))
        } else {
            println("No readable bytes ${buf.readableBytes()}")
        }
    }

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        out.writeShort(msg.opcode)
        out.writeShort(msg.content.writerIndex())
        out.writeBytes(msg.content)
    }
}