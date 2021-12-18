package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualProcessUpdateMessage(val processes: VirtualProcessComponent) {
    companion object : PacketEncoder<VirtualProcessUpdateMessage> {
        override fun encode(message: VirtualProcessUpdateMessage): Packet {
            val content = Unpooled.buffer()

            content.writeShort(message.processes.activeProcesses.size)
            message.processes.activeProcesses.forEach { (pid, pc) ->
                if(pc.immediate) {
                    content.writeBoolean(true)
                } else {
                    content.writeBoolean(false)
                    content.writeInt(pid)
                    content.writeSimpleString(pc.name)
                    content.writeLong(pc.elapsedTime)
                    content.writeLong(pc.preferredRunningTime)
                }
            }

            return Packet(3, 0, content)
        }
    }
}