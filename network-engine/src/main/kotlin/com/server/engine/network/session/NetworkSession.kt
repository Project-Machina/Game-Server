package com.server.engine.network.session

import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.*
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

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

    fun sendMessage(message: Any) {
        sendPacket(message.toPacket())
    }

    inline fun <reified M : Any, reified R : Any> handlePacket(handler: PacketHandler<M, R>) {
        incomingHandlerJobs.add(incomingPackets
            .filter { it.opcode == handler.opcode }
            .transform<Packet, M> { handler.decode(it, this@NetworkSession) }
            .transform<M, R> { handler.handle(it) }
            .filter { it !== Unit }
            .onEach { sendPacket(it.toPacket()) }
            .launchIn(NetworkSession))
    }

    fun shutdownGracefully() {
        incomingHandlerJobs.forEach { it.cancel() }
    }

    companion object : CoroutineScope {

        inline fun <reified M : Any> M.toPacket(): Packet {
            val scope = GlobalContext.get().createScope<M>()
            return scope.get { parametersOf(this) }
        }

        val ATTRIBUTE_KEY = AttributeKey.valueOf<NetworkSession>("session")
        val Channel.session: NetworkSession get() = attr(ATTRIBUTE_KEY).get()
        override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }
}
