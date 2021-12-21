package com.server.engine.network.channel

import com.server.engine.network.channel.packets.PacketChannelHandler
import com.server.engine.network.channel.packets.decoder.PacketDecoder
import com.server.engine.network.channel.packets.encoders.PacketHeaderEncoder
import com.server.engine.network.session.NetworkSession
import com.server.engine.network.session.NetworkSession.Companion.ATTRIBUTE_KEY
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import kotlinx.coroutines.asCoroutineDispatcher

class NetworkChannelInitializer(val bootstrap: ServerBootstrap) : ChannelInitializer<NioSocketChannel>() {

    override fun initChannel(ch: NioSocketChannel) {
        ch.attr(ATTRIBUTE_KEY).set(NetworkSession(ch))
        ch.pipeline().addLast(LengthFieldBasedFrameDecoder(
            1048576,
            0,
            4,
            0,
            4
        ))

        ch.pipeline().addLast(PacketDecoder())
        ch.pipeline().addLast(PacketHeaderEncoder())
        ch.pipeline().addLast(PacketChannelHandler(bootstrap.config().childGroup().asCoroutineDispatcher()))
    }
}