package com.server.engine.network.channel.packets

import com.server.engine.network.channel.login.LoginResponse
import com.server.engine.network.channel.login.NetworkLoginHandler
import com.server.engine.network.session.NetworkSession.Companion.session
import com.server.engine.utilities.inject
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class PacketChannelHandler : SimpleChannelInboundHandler<Packet>() {

    private val loginHandler: NetworkLoginHandler by inject()

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        //Login
        if(msg.opcode == 0) {
            val response = loginHandler.handle(loginHandler.decode(msg, ctx.channel().session))
            val packet = LoginResponse.encode(response)
            ctx.writeAndFlush(packet)
        } else {
            val session = ctx.channel().session
            session.receivePacket(msg)
        }

    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {}
}