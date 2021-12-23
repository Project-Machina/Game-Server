package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.accounts.SystemAccount
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class SystemAccountUpdateMessage(val account: SystemAccount? = null) {
    companion object : PacketEncoder<SystemAccountUpdateMessage> {
        override fun encode(message: SystemAccountUpdateMessage): Packet {
            val buf = Unpooled.buffer()
            buf.writeBoolean(message.account == null)
            if (message.account != null) {
                message.account.let {
                    buf.writeSimpleString(it.username)
                    buf.writeByte(it.permissions.size)
                    it.permissions.forEach {  p ->
                        buf.writeSimpleString(p.key)
                    }
                }
            } else {
                println("Account nulled, not logged in!")
            }
            return Packet(SYSTEM_ACCOUNT_UPDATE, buf)
        }
    }
}