package com.server.engine.dispatchers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object GameDispatcher : CoroutineScope {
    val GAME_DISPATCHER = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext: CoroutineContext = GAME_DISPATCHER
}