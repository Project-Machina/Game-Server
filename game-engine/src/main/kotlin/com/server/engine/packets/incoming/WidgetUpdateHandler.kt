package com.server.engine.packets.incoming

import com.server.engine.game.entity.character.components.WidgetManagerComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.message.WidgetChangeMessage
import com.server.engine.utilities.readSimpleString

class WidgetUpdateHandler(val player: Player) : PacketHandler<WidgetChangeMessage, Unit> {
    override val opcode: Int = 4
    override fun decode(packet: Packet, session: NetworkSession) : WidgetChangeMessage {
        return WidgetChangeMessage(packet.content.readSimpleString())
    }

    override suspend fun handle(message: WidgetChangeMessage) {
        val widgetManager = player.component<WidgetManagerComponent>()
        widgetManager.currentWidget.value = message.widget
    }
}