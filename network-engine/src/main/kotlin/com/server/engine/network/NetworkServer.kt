package com.server.engine.network

import com.server.engine.network.channel.NetworkChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.util.concurrent.ThreadFactory

class NetworkServer {

    private val bootstrap = ServerBootstrap()
    private val bossGroup = NioEventLoopGroup(2, ThreadFactory { Thread().also { it.isDaemon = true } })
    private val workerGroup = NioEventLoopGroup { Thread().also { it.isDaemon = true } }

    fun start(port: Int) {
        try {
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .handler(NetworkChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            val future = bootstrap.bind(port).sync()
            future.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }

}