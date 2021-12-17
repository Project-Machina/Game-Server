package com.server.engine.game.entity

fun interface TickingEntity {

    suspend fun onTick()

}