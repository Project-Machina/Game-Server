package com.server.engine.game

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.software.SoftwareComponent
import com.server.engine.game.software.component.TextComponent
import com.server.engine.game.software.component.VersionedComponent
import com.server.engine.game.vms.VMComponent
import com.server.engine.game.vms.components.hdd.HardDriveComponent
import com.server.engine.game.vms.components.NetworkCardComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.InternetProtocolManager
import com.server.engine.game.world.tick.GameTick
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

val koinModule = module {
    single { InternetProtocolManager() }
    single { GameTick() }
    single { GameWorld() }
}

val softCompsModule = module {
    single<ComponentFactory<out SoftwareComponent>>(named(TextComponent::class.simpleName!!)) { TextComponent }
    single<ComponentFactory<out SoftwareComponent>>(named(VersionedComponent::class.simpleName!!)) { VersionedComponent }
}

val vmCompsModule = module {
    single<ComponentFactory<out VMComponent>>(named(NetworkCardComponent::class.simpleName!!)) { NetworkCardComponent }
    single<ComponentFactory<out VMComponent>>(named(HardDriveComponent::class.simpleName!!)) { HardDriveComponent }
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