package com.server.engine.network.channel

import com.server.engine.network.channel.packets.PacketHandler
import com.server.engine.network.channel.packets.PacketHeaderCodec
import com.server.engine.network.session.NetworkSession
import com.server.engine.network.session.NetworkSession.Companion.ATTRIBUTE_KEY
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler

class NetworkChannelInitializer : ChannelInitializer<NioSocketChannel>() {
    override fun initChannel(ch: NioSocketChannel) {

        ch.attr(ATTRIBUTE_KEY).set(NetworkSession(ch))
        ch.pipeline().addLast("headerCodec", PacketHeaderCodec())
        ch.pipeline().addLast("handler", PacketHandler())

    }
}