package com.server.engine.game

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.components.NetworkCardComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.components.power.PowerStorageComponent
import com.server.engine.game.entity.vms.processes.ProcessComponent
import com.server.engine.game.entity.vms.processes.components.logs.EventDeleteComponent
import com.server.engine.game.entity.vms.software.SoftwareComponent
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.component.TextComponent
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.InternetProtocolManager
import com.server.engine.game.world.tick.GameTick
import com.server.engine.game.world.tick.events.LoginSubscription
import com.server.engine.game.world.tick.events.WorldTick
import com.server.engine.network.channel.login.NetworkLoginHandler
import com.server.engine.packets.login.LoginHandler
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val koinModule = module {
    single { InternetProtocolManager() }
    single { GameTick() }
    single { WorldTick() }
    single { GameWorld() }
    single { LoginHandler() } bind NetworkLoginHandler::class
}

val subscriptionModule = module {
    single { LoginSubscription() }
}

val softCompsModule = module {
    single<ComponentFactory<out SoftwareComponent>>(named(TextComponent::class.simpleName!!)) { TextComponent }
    single<ComponentFactory<out SoftwareComponent>>(named(VersionedComponent::class.simpleName!!)) { VersionedComponent }
    single<ComponentFactory<out SoftwareComponent>>(named(ProcessOwnerComponent::class.simpleName!!)) { ProcessOwnerComponent }
    single<ComponentFactory<out SoftwareComponent>>(named(VisibleComponent::class.simpleName!!)) { VisibleComponent }
}

val vmCompsModule = module {
    single<ComponentFactory<out VMComponent>>(named(NetworkCardComponent::class.simpleName!!)) { NetworkCardComponent }
    single<ComponentFactory<out VMComponent>>(named(HardDriveComponent::class.simpleName!!)) { HardDriveComponent }
    single<ComponentFactory<out VMComponent>>(named(PowerStorageComponent::class.simpleName!!)) { PowerStorageComponent }
    single<ComponentFactory<out VMComponent>>(named(MotherboardComponent::class.simpleName!!)) { MotherboardComponent }
    single<ComponentFactory<out VMComponent>>(named(StorageRackComponent::class.simpleName!!)) { StorageRackComponent }
}

val processBehaviourModule = module {
}