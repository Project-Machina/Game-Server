package com.server.engine.network.channel.packets

fun interface PacketDecoder<T> {

    fun decode(packet: Packet) : T

}