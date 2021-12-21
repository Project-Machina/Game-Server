package com.server.engine.network.channel.packets

import com.server.engine.network.channel.login.LoginResponse
import com.server.engine.network.channel.login.NetworkLoginHandler
import com.server.engine.network.session.NetworkSession.Companion.session
import com.server.engine.utilities.inject
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PacketChannelHandler(val nettyDispatcher: CoroutineDispatcher) : SimpleChannelInboundHandler<Packet>() {
    private val loginHandler: NetworkLoginHandler by inject()
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        //Login
        if(msg.opcode == 0) {
            flowOf(loginHandler)
                .onEach {
                    val response = it.handle(it.decode(msg, ctx.channel().session))
                    ctx.writeAndFlush(response)
                }.launchIn(CoroutineScope(nettyDispatcher))
        } else {
            val session = ctx.channel().session
            session.receivePacket(msg)
        }

    }
}