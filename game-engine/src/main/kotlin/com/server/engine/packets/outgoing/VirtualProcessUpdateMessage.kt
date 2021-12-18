package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualProcessUpdateMessage(val process: VirtualProcess) {
    companion object : PacketEncoder<VirtualProcessUpdateMessage> {
        override fun encode(message: VirtualProcessUpdateMessage): Packet {
            val content = Unpooled.buffer()
            val pc = message.process

            if(pc.immediate) {
                content.writeBoolean(true)
            } else {
                content.writeBoolean(false)
                content.writeInt(pc.pid)
                content.writeSimpleString(pc.name)
                content.writeLong(pc.elapsedTime)
                content.writeLong(pc.preferredRunningTime)
            }

            return Packet(3, 0, content)
        }
    }
}