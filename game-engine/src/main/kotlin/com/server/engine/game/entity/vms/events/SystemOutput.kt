package com.server.engine.game.entity.vms.events

import com.server.engine.game.entity.character.npcs.Npc
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine

interface SystemOutput<T> {

    val vm: VirtualMachine
    val source: T
    val isRemote: Boolean
        get() = false

    suspend fun handleEventForPlayer(player: Player, isRemote: Boolean)
    suspend fun handleEventForNpc(npc: Npc){}

}