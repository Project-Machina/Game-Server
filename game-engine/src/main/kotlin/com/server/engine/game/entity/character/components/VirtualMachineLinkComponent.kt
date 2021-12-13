package com.server.engine.game.entity.character.components

import com.server.engine.game.vms.VMComponent
import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class VirtualMachineLinkComponent : VMComponent {

    private val world: GameWorld by inject()

    val linkIP = MutableStateFlow("localhost")

    val linkVM: VirtualMachine
        get() = world.publicVirtualMachines[linkIP.value] ?: error("Not linked to any vm.")

    fun link(address: String) {
        linkIP.value = address
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