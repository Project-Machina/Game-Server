package com.server.engine.network.channel

import com.server.engine.network.channel.packets.PacketChannelHandler
import com.server.engine.network.channel.packets.codec.PacketCodec
import com.server.engine.network.channel.packets.decoder.PacketDecoder
import com.server.engine.network.channel.packets.encoders.PacketHeaderEncoder
import com.server.engine.network.session.NetworkSession
import com.server.engine.network.session.NetworkSession.Companion.ATTRIBUTE_KEY
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder

class NetworkChannelInitializer : ChannelInitializer<NioSocketChannel>() {

    override fun initChannel(ch: NioSocketChannel) {
        ch.attr(ATTRIBUTE_KEY).set(NetworkSession(ch))
        ch.pipeline().addLast(LengthFieldBasedFrameDecoder(
            8192,
            0,
            4,
            0,
            4
        ))
        ch.pipeline().addLast(PacketDecoder())
        ch.pipeline().addLast(PacketHeaderEncoder())
        ch.pipeline().addLast(PacketChannelHandler())
    }
}