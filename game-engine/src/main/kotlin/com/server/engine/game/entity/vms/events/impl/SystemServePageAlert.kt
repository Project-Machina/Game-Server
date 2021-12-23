package com.server.engine.game.entity.vms.events.impl

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.components.pages.HomePageComponent
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.packets.outgoing.NpcPageMessage

class SystemServePageAlert(override val vm: VirtualMachine, override val source: VirtualMachine) : SystemOutput<VirtualMachine> {

    override suspend fun handleEventForPlayer(player: Player, isRemote: Boolean) {
        if(source.has<HomePageComponent>()) {
            val home = source.component<HomePageComponent>()
            player.session.sendMessage(NpcPageMessage(home.name))
        }
    }
}