package com.server.engine.network.channel.packets

interface PacketCodec<TD, TE> : PacketDecoder<TD>, PacketEncoder<TE>