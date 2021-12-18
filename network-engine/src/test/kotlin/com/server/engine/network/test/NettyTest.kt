package com.server.engine.network.test

import com.server.engine.network.channel.packets.Packet
import com.server.engine.utilities.readSimpleString
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.codec.MessageToMessageDecoder
import org.junit.jupiter.api.Test


class NettyTest {

    @Test
    fun `field length test`() {

        val ch = EmbeddedChannel(
            LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4),
            PacketHeaderEncoder(),
            PacketDecoder()
        )

        val buf = Unpooled.buffer()
        buf.writeSimpleString("Hello, World")

        ch.writeOutbound(Packet(1, -1, buf))
        ch.writeInbound(ch.readOutbound())

        val msg = ch.readInbound<Packet>()

        println(msg.opcode)
        println(msg.content.readSimpleString())

    }

    class PacketHeaderEncoder : MessageToByteEncoder<Packet>() {
        override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
            out.writeInt(msg.content.readableBytes() + 2)
            out.writeShort(msg.opcode)
            out.writeBytes(msg.content)
        }
    }

    class PacketDecoder : MessageToMessageDecoder<ByteBuf>() {
        override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
            val opcode = msg.readUnsignedShort()
            val frame = msg.copy()
            out.add(Packet(opcode, -1, frame))
        }
    }

}