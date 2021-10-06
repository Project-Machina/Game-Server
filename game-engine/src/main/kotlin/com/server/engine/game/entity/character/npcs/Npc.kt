package com.server.engine.game.entity.character.npcs

import com.server.engine.game.entity.character.Character
import com.server.engine.game.world.tick.Subscription
import kotlinx.coroutines.flow.MutableStateFlow

class Npc : Character() {

    private val _subscription = MutableStateFlow<Subscription<Npc>?>(null)
    override val subscription: Subscription<Npc>? by _subscription

    override suspend fun onTick() {

    }

    override fun isActive(): Boolean {
        return true
    }
}