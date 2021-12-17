package com.server.engine.game.entity.vms.events

import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine

interface UpdateEvent<T : VMComponent> {

    val source: VirtualMachine
    val comp: T

    fun handleEvent(player: Player)

}