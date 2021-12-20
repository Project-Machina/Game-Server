package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualSoftwareUpdateMessage(
    val software: VirtualSoftware
) {

    companion object : PacketEncoder<VirtualSoftwareUpdateMessage> {
        override fun encode(message: VirtualSoftwareUpdateMessage): Packet {
            val buf = Unpooled.buffer()
            val soft = message.software
            buf.writeSimpleString(soft.id())
            buf.writeSimpleString(soft.name)
            buf.writeSimpleString(soft.extension)
            if(soft.has<VersionedComponent>()) {
                val version = soft.component<VersionedComponent>()
                buf.writeDouble(version.version)
            } else {
                buf.writeDouble(0.0)
            }
            buf.writeLong(soft.size)
            if(soft.has<ProcessOwnerComponent>()) {
                val pid = soft.component<ProcessOwnerComponent>()
                buf.writeInt(pid.pid)
            } else {
                buf.writeInt(-1)
            }
            return Packet(4, buf)
        }
    }
}