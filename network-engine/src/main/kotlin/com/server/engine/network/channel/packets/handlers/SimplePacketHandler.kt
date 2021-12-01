package com.server.engine.network.channel.packets.handlers

import com.server.engine.network.channel.packets.PacketDecoder

interface SimplePacketHandler<M> : PacketHandler<M, Unit>, PacketDecoder<M>