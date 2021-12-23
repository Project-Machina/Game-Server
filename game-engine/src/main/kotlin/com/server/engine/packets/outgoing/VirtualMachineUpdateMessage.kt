package com.server.engine.packets.outgoing

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.NetworkCardComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.inject
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled

class VirtualMachineUpdateMessage(val vm: VirtualMachine, val isLinked: Boolean = false) {
    companion object : PacketEncoder<VirtualMachineUpdateMessage> {
        private val world: GameWorld by inject()
        override fun encode(message: VirtualMachineUpdateMessage): Packet {
            val buf = Unpooled.buffer()
            val vm = message.vm
            val ip = world.vmToAddress[message.vm]
            val domain = world.addressToDomain[ip]
            val mb = vm.component<MotherboardComponent>()
            val rack = vm.component<StorageRackComponent>()
            val network = vm.component<NetworkCardComponent>()

            buf.writeSimpleString(vm.id.toString(), true)
            buf.writeSimpleString(vm.name)
            buf.writeSimpleString(ip ?: "none")
            buf.writeSimpleString(domain ?: "none")
            buf.writeBoolean(message.isLinked)

            buf.writeLong(mb.ramCapacity)
            buf.writeLong(mb.availableRam)

            buf.writeLong(rack.maxCapacity)
            buf.writeLong(rack.availableSpace)

            buf.writeInt(mb.threadCapacity)
            buf.writeInt(mb.availableThreads)

            buf.writeInt(mb.networkCardCapacity)
            buf.writeInt(mb.availableNetwork)

            buf.writeInt(mb.powerConsumption.watts)
            buf.writeInt(rack.powerConsumption.watts)
            buf.writeInt(network.powerConsumption.watts)

            println(buf.readableBytes())

            return Packet(VIRTUAL_MACHINE_UPDATE,  buf)
        }
    }
}