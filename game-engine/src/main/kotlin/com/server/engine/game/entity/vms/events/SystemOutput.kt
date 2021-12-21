package com.server.engine.game.entity.vms.events

import com.server.engine.game.entity.character.npcs.Npc
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine

interface SystemOutput<T> {

    val vm: VirtualMachine
    val source: T

    suspend fun handleEventForPlayer(player: Player)
    suspend fun handleEventForNpc(npc: Npc){}

}