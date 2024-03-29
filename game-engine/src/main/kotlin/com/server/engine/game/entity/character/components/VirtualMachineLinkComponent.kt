package com.server.engine.game.entity.character.components

import com.server.engine.dispatchers.PlayerDispatcher
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.vlog
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class VirtualMachineLinkComponent(val source: Player) : com.server.engine.game.entity.vms.VMComponent {

    private val world: GameWorld by inject()
    private val monitorJobs = mutableListOf<Job>()
    val remoteMonitorJobs = mutableListOf<Job>()

    val linkIP = MutableStateFlow("localhost")

    val linkVM: VirtualMachine
        get() = world.publicVirtualMachines[linkIP.value] ?: error("Not linked to any vm.")

    fun linkTo(address: String) {
        val vm = world.publicVirtualMachines[address]
        if(vm != null && source.controlledVirtualMachines.contains(vm.id)) {
            if (linkIP.value != address && monitorJobs.isNotEmpty()) {
                monitorJobs.forEach(Job::cancel)
            }
            linkIP.value = address
            source.lastControlledMachine = vm.id
            monitorJobs.add(linkVM.systemOutput
                .onEach { it.handleEventForPlayer(source, false) }
                .launchIn(PlayerDispatcher))
            val accman = linkVM.component<SystemAccountComponent>()
            if (accman.login("link", "root", "")) {
                linkVM.vlog("link", "logged in as root.")
            }
        }
    }

    fun stopMonitorJobs() {
        monitorJobs.forEach(Job::cancel)
    }

    fun stopRemoteMonitorJobs() {
        remoteMonitorJobs.forEach(Job::cancel)
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("link", linkIP.value)
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("link")) {
            linkIP.value = json["link"]!!.jsonPrimitive.content
        }
    }
}