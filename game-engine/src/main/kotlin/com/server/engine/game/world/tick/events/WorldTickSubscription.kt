package com.server.engine.game.world.tick.events

import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.TickSubscription
import com.server.engine.utilities.inject

class WorldTickSubscription : TickSubscription {

    private val world: GameWorld by inject()

    override suspend fun onTick() {
        world.publicVirtualMachines.values.forEach { it.onTick() }
    }
}