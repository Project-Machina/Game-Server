package com.server.engine.game

import com.server.engine.game.world.InternetProtocolManager
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

val koinModule = module {
    single { InternetProtocolManager() }
}

inline fun <reified C : Any> inject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
    noinline parameters: ParametersDefinition? = null
) = GlobalContext.get().inject<C>(qualifier, mode, parameters)

inline fun <reified C : Any> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = GlobalContext.get().get<C>(qualifier, parameters)