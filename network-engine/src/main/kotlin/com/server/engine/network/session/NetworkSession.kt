package com.server.engine.network.session

import com.server.engine.network.channel.packets.Packet
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class NetworkSession(private val channel: Channel) {

    val incomingPackets = MutableSharedFlow<Packet>(extraBufferCapacity = 255)

    fun receivePacket(packet: Packet) : Boolean {
        return incomingPackets.tryEmit(packet)
    }

    fun sendPacket(packet: Packet, flush: Boolean = true) {
        channel.write(packet)
        if(flush) {
            channel.flush()
        }
    }

    companion object : CoroutineScope {
        val ATTRIBUTE_KEY = AttributeKey.valueOf<NetworkSession>("session")
        val Channel.session: NetworkSession get() = attr(ATTRIBUTE_KEY).get()
        override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

}