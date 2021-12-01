package com.server.engine.network.channel.packets

import com.server.engine.network.session.NetworkSession

fun interface PacketDecoder<T> {

    fun decode(packet: Packet, session: NetworkSession) : T

}