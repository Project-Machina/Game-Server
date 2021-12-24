package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.packets.outgoing.ParameterMessage

class SystemParameter(
    override val vm: VirtualMachine,
    vararg val params: Pair<String, Any>
) : SystemOutput<Unit> {
    override val source: Unit = Unit
    override suspend fun handleEventForPlayer(player: Player, isRemote: Boolean) {
        player.session.sendMessage(ParameterMessage(
            mutableMapOf(*params)
        ))
    }
}