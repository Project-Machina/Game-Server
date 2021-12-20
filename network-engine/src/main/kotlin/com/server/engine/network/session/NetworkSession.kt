package com.server.engine.network.session

import com.server.engine.dispatchers.GameDispatcher
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf

class NetworkSession(private val channel: Channel) {

    val incomingPackets = MutableSharedFlow<Packet>(extraBufferCapacity = 255)

    val incomingHandlerJobs = mutableListOf<Job>()

    val isActive: Boolean get() = channel.isActive

    fun receivePacket(packet: Packet): Boolean {
        return incomingPackets.tryEmit(packet)
    }

    fun sendPacket(packet: Packet, flush: Boolean = true) {
        channel.write(packet)
        if (flush) {
            channel.flush()
        }
    }

    inline fun <reified T> sendMessage(message: T) {
        if (message != null) {
            sendPacket(message.toPacket())
        }
    }

    inline fun <reified M : Any, reified R : Any> handlePacket(handler: PacketHandler<M, R>) {
        incomingPackets
            .filter { it.opcode == handler.opcode }
            .transform {
                val msg = handler.decode(it, this@NetworkSession)
                it.content.release()
                emit(msg)
            }
            .onEach { handler.handle(it) }
            .launchIn(GameDispatcher)
    }

    fun shutdownGracefully() {
        incomingHandlerJobs.forEach { it.cancel() }
    }

    companion object {

        inline fun <reified M : Any> M.toPacket(): Packet {
            val scope = GlobalContext.get().createScope<M>()
            return scope.get { parametersOf(this) }
        }

        val ATTRIBUTE_KEY = AttributeKey.valueOf<NetworkSession>("session")
        val Channel.session: NetworkSession get() = attr(ATTRIBUTE_KEY).get()

    }
}
