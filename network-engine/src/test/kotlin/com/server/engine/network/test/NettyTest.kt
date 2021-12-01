package com.server.engine.network.test

import com.server.engine.network.channel.packets.Packet
import com.server.engine.utilities.readSimpleString
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.ByteToMessageCodec
import org.junit.jupiter.api.Test


class NettyTest {

    @Test
    fun `field length test`() {

        val ch = EmbeddedChannel(
            PacketCodec()
        )

        val buf = Unpooled.buffer(4)
        buf.writeSimpleString("Hello, World")
        buf.fill()

        println("WriterIndex ${buf.writerIndex()} - ${buf.capacity()}")

        ch.writeOutbound(Packet(5, -1, buf))
        ch.writeInbound( ch.readOutbound() )

        val inBuf = ch.readInbound<Packet>()

        /*assert(inBuf.opcode == 5)
        assert(inBuf.content.readSimpleString() == "Hello, World")*/

        /*val packet = ch.readInbound<Packet>()

        println(packet.opcode)
        println(packet.content.readSimpleString())*/

    }

    private fun ByteBuf.fill() {
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
        writeSimpleString("Hello, World")
    }

    class PacketCodec : ByteToMessageCodec<Packet>() {
        override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
            println("Encoding $msg")
            out.writeShort(msg.opcode)
            val frameLength = msg.content.writerIndex()
            out.writeShort(frameLength)
            out.writeBytes(msg.content)
        }
        override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
            println("Decoding ${buf.readableBytes()}")
            if(buf.readableBytes() >= 3) {
                val opcode = buf.readUnsignedShort()
                val frameLength = buf.readByte().toInt()

                println(opcode)
                println(frameLength)

                /*val content = buf.readBytes(frameLength)
                out.add(Packet(opcode, -1, content))*/
            }
        }
    }
}