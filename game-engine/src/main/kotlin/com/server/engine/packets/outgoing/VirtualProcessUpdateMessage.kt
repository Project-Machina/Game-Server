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
            message.processes.activeProcesses.forEachIndexed { index, virtualProcess ->
                if(virtualProcess.immediate) {
                    content.writeBoolean(true)
                } else {
                    content.writeBoolean(false)
                    content.writeShort(index)
                    content.writeSimpleString(virtualProcess.name)
                    content.writeLong(virtualProcess.elapsedTime)
                    content.writeLong(virtualProcess.preferredRunningTime)
                }
            }

            return Packet(3, 0, content)
        }
    }
}