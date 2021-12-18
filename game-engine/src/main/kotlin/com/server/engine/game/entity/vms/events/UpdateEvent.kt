package com.server.engine.game.entity.vms.events

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine

interface UpdateEvent<T> {

    val vm: VirtualMachine
    val source: T

    fun handleEvent(player: Player)

}