package com.server.engine.game.world.tick

fun interface TickSubscription {

    suspend fun onTick()

}