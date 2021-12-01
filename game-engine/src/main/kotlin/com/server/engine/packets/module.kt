package com.server.engine.packets

import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.network.channel.login.LoginResponse
import org.koin.core.module.Module
import org.koin.dsl.module

val outgoingPacketModule = module {
    MessageEncoder(LoginResponse)
}

inline fun <reified M : Any> Module.MessageEncoder(encoder: PacketEncoder<M>) {
    scope<M> {
        scoped { encoder }
        factory { (msg: M) ->
            encoder.encode(msg)
        }
    }
}