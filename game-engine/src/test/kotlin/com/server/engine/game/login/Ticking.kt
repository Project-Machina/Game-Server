package com.server.engine.game.login

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.InternetProtocolManager
import com.server.engine.game.world.tick.VirtualMachineTick
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.network.session.NetworkSession
import com.server.engine.utilities.inject
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.util.Attribute
import io.netty.util.AttributeKey
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.net.SocketAddress

class Ticking {

    @Test
    fun `login tick`() {

        startKoin {
            modules(module {
                single { InternetProtocolManager() }
                single { VirtualMachineTick() }
                single { GameWorld() }
            })
        }


        val player = Player("Javatar", NetworkSession(DummyChannel()))
        val tick: VirtualMachineTick by inject()
        val loginSub = LoginSubscription()

        tick.subscribe(loginSub)

        loginSub.loginQueue.add(player)

        //while(true);

        stopKoin()
    }

    class DummyChannel : Channel {
        override fun <T : Any?> attr(key: AttributeKey<T>?): Attribute<T> {
            TODO("Not yet implemented")
        }

        override fun <T : Any?> hasAttr(key: AttributeKey<T>?): Boolean {
            TODO("Not yet implemented")
        }

        override fun bind(localAddress: SocketAddress?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun bind(localAddress: SocketAddress?, promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun connect(remoteAddress: SocketAddress?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun connect(remoteAddress: SocketAddress?, localAddress: SocketAddress?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun connect(remoteAddress: SocketAddress?, promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun connect(
            remoteAddress: SocketAddress?,
            localAddress: SocketAddress?,
            promise: ChannelPromise?
        ): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun disconnect(): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun disconnect(promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun close(): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun close(promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun deregister(): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun deregister(promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun read(): Channel {
            TODO("Not yet implemented")
        }

        override fun write(msg: Any?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun write(msg: Any?, promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun flush(): Channel {
            TODO("Not yet implemented")
        }

        override fun writeAndFlush(msg: Any?, promise: ChannelPromise?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun writeAndFlush(msg: Any?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun newPromise(): ChannelPromise {
            TODO("Not yet implemented")
        }

        override fun newProgressivePromise(): ChannelProgressivePromise {
            TODO("Not yet implemented")
        }

        override fun newSucceededFuture(): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun newFailedFuture(cause: Throwable?): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun voidPromise(): ChannelPromise {
            TODO("Not yet implemented")
        }

        override fun compareTo(other: Channel?): Int {
            TODO("Not yet implemented")
        }

        override fun id(): ChannelId {
            TODO("Not yet implemented")
        }

        override fun eventLoop(): EventLoop {
            TODO("Not yet implemented")
        }

        override fun parent(): Channel {
            TODO("Not yet implemented")
        }

        override fun config(): ChannelConfig {
            TODO("Not yet implemented")
        }

        override fun isOpen(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isRegistered(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isActive(): Boolean {
            TODO("Not yet implemented")
        }

        override fun metadata(): ChannelMetadata {
            TODO("Not yet implemented")
        }

        override fun localAddress(): SocketAddress {
            TODO("Not yet implemented")
        }

        override fun remoteAddress(): SocketAddress {
            TODO("Not yet implemented")
        }

        override fun closeFuture(): ChannelFuture {
            TODO("Not yet implemented")
        }

        override fun isWritable(): Boolean {
            TODO("Not yet implemented")
        }

        override fun bytesBeforeUnwritable(): Long {
            TODO("Not yet implemented")
        }

        override fun bytesBeforeWritable(): Long {
            TODO("Not yet implemented")
        }

        override fun unsafe(): Channel.Unsafe {
            TODO("Not yet implemented")
        }

        override fun pipeline(): ChannelPipeline {
            TODO("Not yet implemented")
        }

        override fun alloc(): ByteBufAllocator {
            TODO("Not yet implemented")
        }
    }

}