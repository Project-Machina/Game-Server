package com.server.engine.network

import com.server.engine.network.channel.NetworkChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import java.util.concurrent.ThreadFactory

class NetworkServer {

    private val bootstrap = ServerBootstrap()
    private val bossGroup = NioEventLoopGroup(2, ThreadFactory { Thread().also { it.isDaemon = true } })
    private val workerGroup = NioEventLoopGroup { Thread().also { it.isDaemon = true } }

    fun start() {

        bootstrap.group(bossGroup, workerGroup)
            .handler(NetworkChannelInitializer())

    }

}