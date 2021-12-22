package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.entity.vms.software.isHidden
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualSoftwareUpdateMessage(
    val softs: List<VirtualSoftware>,
    val isRemote: Boolean = false
) {

    companion object : PacketEncoder<VirtualSoftwareUpdateMessage> {
        override fun encode(message: VirtualSoftwareUpdateMessage): Packet {
            val buf = Unpooled.buffer()
            val softs = message.softs
            buf.writeShort(softs.size)
            buf.writeBoolean(message.isRemote)
            softs.forEach { soft ->
                buf.writeSimpleString(soft.id(), true)
                buf.writeSimpleString(soft.name)
                buf.writeSimpleString(soft.extension)
                buf.writeBoolean(soft.isHidden())
                if (soft.has<VersionedComponent>()) {
                    val version = soft.component<VersionedComponent>()
                    buf.writeDouble(version.version)
                } else {
                    buf.writeDouble(0.0)
                }
                buf.writeLong(soft.size)
                if (soft.has<ProcessOwnerComponent>()) {
                    val pid = soft.component<ProcessOwnerComponent>()
                    buf.writeInt(pid.pid)
                } else {
                    buf.writeInt(-1)
                }
            }
            return Packet(VIRTUAL_SOFTWARE_UPDATE, buf)
        }
    }
}