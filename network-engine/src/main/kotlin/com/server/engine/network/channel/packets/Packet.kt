package com.server.engine.network.channel.packets

import io.netty.buffer.ByteBuf

data class Packet(val opcode: Int, val type: Int, val content: ByteBuf)