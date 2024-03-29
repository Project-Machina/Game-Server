package com.server.engine.network.channel.packets.handlers

import com.server.engine.network.channel.packets.PacketDecoder

interface PacketHandler<M, R : Any> : PacketDecoder<M> {

    val opcode: Int

    suspend fun handle(message: M) : R

}