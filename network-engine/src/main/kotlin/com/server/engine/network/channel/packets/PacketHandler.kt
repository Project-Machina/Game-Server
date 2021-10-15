package com.server.engine.network.channel.packets

import com.server.engine.network.session.NetworkSession.Companion.session
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class PacketHandler : SimpleChannelInboundHandler<Packet>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        val session = ctx.channel().session
        session.receivePacket(msg)
    }
}