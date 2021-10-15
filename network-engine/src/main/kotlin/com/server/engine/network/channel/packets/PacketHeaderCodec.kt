package com.server.engine.network.channel.packets

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import java.lang.IndexOutOfBoundsException

class PacketHeaderCodec : ByteToMessageCodec<Packet>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        val (opcode, type, content) = msg
        if(type in 0..2) {
            out.writeByte(opcode)
            when (type) {
                1 -> {
                    out.writeShort(content.writerIndex())
                }
                2 -> {
                    out.writeInt(content.writerIndex())
                }
                else -> {
                    out.writeByte(content.writerIndex())
                }
            }
        } else if(type in 3..5) {
            out.writeShort(opcode)
            when (type) {
                1 -> {
                    out.writeShort(content.writerIndex())
                }
                2 -> {
                    out.writeInt(content.writerIndex())
                }
                else -> {
                    out.writeByte(content.writerIndex())
                }
            }
        }
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= 2) {
            val type = buf.readUnsignedByte().toInt()
            try {
                val (opcode, size) = when (type) {
                    in 0..2 -> {
                        val opcode = buf.readUnsignedByte().toInt()
                        val size = when (type) {
                            1 -> {
                                buf.readUnsignedShort()
                            }
                            2 -> {
                                buf.readInt()
                            }
                            else -> {
                                buf.readUnsignedByte().toInt()
                            }
                        }
                        opcode to size
                    }
                    in 3..5 -> {
                        val opcode = buf.readUnsignedShort()
                        val size = when(type) {
                            4 -> {
                                buf.readUnsignedShort()
                            }
                            5 -> {
                                buf.readInt()
                            }
                            else -> {
                                buf.readUnsignedByte().toInt()
                            }
                        }
                        opcode to size
                    }
                    else -> -1 to -1
                }
                if(opcode == -1 || size == -1)
                    return

                if(buf.readableBytes() >= size) {
                    out.add(Packet(opcode, type, buf.readBytes(size).copy()))
                }
            } catch (e: IndexOutOfBoundsException) {
                buf.release(buf.refCnt())
                return
            }
        }
    }
}