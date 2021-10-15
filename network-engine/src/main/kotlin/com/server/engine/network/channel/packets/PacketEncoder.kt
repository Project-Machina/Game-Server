package com.server.engine.network.channel.packets

fun interface PacketEncoder<T> {

    fun encode(message: T) : Packet

}