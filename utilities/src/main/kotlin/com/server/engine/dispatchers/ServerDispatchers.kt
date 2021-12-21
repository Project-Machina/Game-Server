package com.server.engine.dispatchers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object VirtualMachineDispatcher : CoroutineScope, CoroutineContext {
    private val GAME_DISPATCHER = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext: CoroutineContext = GAME_DISPATCHER
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
        return coroutineContext.fold(initial, operation)
    }

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        return coroutineContext[key]
    }

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        return coroutineContext.minusKey(key)
    }
}

object PlayerDispatcher : CoroutineScope, CoroutineContext {
    private val PlAYER_DISPATCHER = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext: CoroutineContext = PlAYER_DISPATCHER
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
        return VirtualMachineDispatcher.coroutineContext.fold(initial, operation)
    }

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        return VirtualMachineDispatcher.coroutineContext[key]
    }

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        return VirtualMachineDispatcher.coroutineContext.minusKey(key)
    }
}

object NpcDispatcher : CoroutineScope, CoroutineContext {
    private val NPC_DISPATCHER = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext: CoroutineContext = NPC_DISPATCHER
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
        return VirtualMachineDispatcher.coroutineContext.fold(initial, operation)
    }

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        return VirtualMachineDispatcher.coroutineContext[key]
    }

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        return VirtualMachineDispatcher.coroutineContext.minusKey(key)
    }
}