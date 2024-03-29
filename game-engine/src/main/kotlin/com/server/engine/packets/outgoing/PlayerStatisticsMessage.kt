package com.server.engine.packets.outgoing

import com.server.engine.game.entity.character.components.RankComponent
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.get
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled
import java.time.LocalDateTime
import java.time.ZoneOffset

class PlayerStatisticsMessage(val player: Player) {
    companion object : PacketEncoder<PlayerStatisticsMessage> {
        override fun encode(message: PlayerStatisticsMessage): Packet {
            val world: GameWorld = get()
            val content = Unpooled.buffer()
            val player = message.player
            val linkComp = player.component<VirtualMachineLinkComponent>()
            val linkIP = linkComp.linkIP.value
            val vm = linkComp.linkVM
            val remoteComp = vm.component<ConnectionComponent>()
            val remoteVM = remoteComp.remoteVM
            val remoteIP = remoteComp.remoteAddress.value
            val rankComp = player.component<RankComponent>()

            val storageRack = vm.component<StorageRackComponent>()
            val hardDisk = vm.component<HardDriveComponent>()
            val motherboard = vm.component<MotherboardComponent>()
            val pcm = vm.component<VirtualProcessComponent>()

            val domain = if(world.validateDomain(remoteIP)) remoteIP else world.addressToDomain[remoteIP]

            val remoteAccman = remoteVM?.component<SystemAccountComponent>()

            val acc = remoteAccman?.getActiveAccountFor(linkIP)

            val remoteValue = if(acc != null) {
                (domain ?: remoteIP) + " (${acc.username})"
            } else domain ?: remoteIP

            content.writeSimpleString(linkIP)
            content.writeSimpleString(remoteValue)
            content.writeLong(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
            content.writeInt(rankComp.rank)
            content.writeInt(rankComp.nextRankProgress)
            content.writeInt(rankComp.experienceForNextRank)

            content.writeSimpleString(storageRack.name)
            content.writeLong(storageRack.availableSpace)
            content.writeLong(hardDisk.driveUsage)

            content.writeSimpleString(motherboard.name)
            content.writeLong(motherboard.availableRam)
            content.writeLong(pcm.ramUsage)

            content.writeInt(motherboard.availableThreads)
            content.writeInt(pcm.threadUsage)
            content.writeInt(pcm.activeProcesses.size)

            return Packet(PLAYER_STATS, content)
        }
    }
}