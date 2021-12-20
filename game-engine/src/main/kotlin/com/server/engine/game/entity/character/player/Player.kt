package com.server.engine.game.entity.character.player

import com.server.engine.game.entity.character.Character
import com.server.engine.game.entity.character.components.RankComponent
import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.components.WidgetManagerComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.Subscription
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.incoming.LogoutHandler
import com.server.engine.packets.incoming.PingHandler
import com.server.engine.packets.incoming.VmCommandHandler
import com.server.engine.packets.incoming.WidgetUpdateHandler
import com.server.engine.packets.outgoing.PlayerStatisticsMessage
import com.server.engine.packets.outgoing.VirtualMachineUpdateMessage
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.concurrent.CancellationException

class Player(val name: String, val session: NetworkSession) : Character() {

    private val world: GameWorld by inject()

    var lastControlledMachine: UUID? = null

    val controlledVirtualMachines = mutableListOf<UUID>()

    private val _subscription = MutableStateFlow<Subscription<Player>?>(null)
    override var subscription: Subscription<Player>? by _subscription

    fun onLogin() {
        with(VirtualMachineLinkComponent(this))
        with(WidgetManagerComponent())
        with(RankComponent())

        if (name.lowercase() == "javatar") {
            val link = component<VirtualMachineLinkComponent>()
            val vm = world.publicVirtualMachines["74.97.118.97"]
            if(vm != null) {
                controlledVirtualMachines.add(vm.id)
                link.linkTo("74.97.118.97")
            }
        }

        session.handlePacket(PingHandler())
        session.handlePacket(VmCommandHandler(player = this))
        session.handlePacket(LogoutHandler(player = this))
        session.handlePacket(WidgetUpdateHandler(player = this))
    }

    fun logout() {
        if (subscription != null) {
            subscription?.cancel(CancellationException("Logging out player $name"))
            println("Logging out player $name.")
        }
    }

    override suspend fun onTick() {
        session.sendMessage(PlayerStatisticsMessage(this))
        if(component<WidgetManagerComponent>().currentWidget.value == "hardware") {
            controlledVirtualMachines.forEach { uuid ->
                val vm = world.virtualMachines[uuid]
                if(vm != null) {
                    session.sendMessage(VirtualMachineUpdateMessage(vm, vm === component<VirtualMachineLinkComponent>().linkVM))
                }
            }
        }
    }

    override fun isActive(): Boolean {
        return subscription?.isActive ?: false
    }
}
