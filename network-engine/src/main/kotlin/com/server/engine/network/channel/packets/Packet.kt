package com.server.engine.network.channel.packets

import io.netty.buffer.ByteBuf

class Packet(val opcode: Int, val type: Int, val content: ByteBuf) {



    operator fun component1() = opcode
    operator fun component2() = type
    operator fun component3() = content

}