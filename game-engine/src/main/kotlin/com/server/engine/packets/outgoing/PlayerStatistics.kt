package com.server.engine.packets.outgoing

import com.server.engine.game.entity.character.components.RankComponent
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.vms.VirtualMachine.Companion.component
import com.server.engine.game.vms.components.connection.ConnectionComponent
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.PacketEncoder
import com.server.engine.utilities.writeSimpleString
import io.netty.buffer.Unpooled
import java.time.LocalDateTime
import java.time.ZoneOffset

class PlayerStatistics(val player: Player) {
    companion object : PacketEncoder<PlayerStatistics> {
        override fun encode(message: PlayerStatistics): Packet {
            val content = Unpooled.buffer()
            val player = message.player
            val linkComp = player.component<VirtualMachineLinkComponent>()
            val linkIP = linkComp.linkIP.value
            val remoteComp = linkComp.linkVM.component<ConnectionComponent>()
            val remoteIP = remoteComp.remoteIP.value
            val domain = remoteComp.domain.value
            val rankComp = player.component<RankComponent>()

            content.writeSimpleString(linkIP)
            content.writeSimpleString(if(domain != "none") domain else remoteIP)
            content.writeLong(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
            content.writeInt(rankComp.rank)
            content.writeInt(rankComp.nextRankProgress)
            content.writeInt(rankComp.experienceForNextRank)

            return Packet(2, 0, content)
        }
    }
}