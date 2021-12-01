package com.server.engine.game.entity.character.components

import com.server.engine.game.components.Component
import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.world.GameWorld
import com.server.engine.utilities.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class VirtualMachineLinkComponent : Component {

    private val world: GameWorld by inject()

    val link = MutableStateFlow("127.0.0.1")

    val linkedVM: VirtualMachine
        get() = world.publicVirtualMachines[link.value] ?: error("Not linked to any vm.")

    val connectedTo = MutableStateFlow("127.0.0.1")

    val connectedVM: VirtualMachine
        get() = world.publicVirtualMachines[connectedTo.value] ?: error("Not connected to any vm.")

    fun link(address: String) {
        link.value = address
    }

    fun connect(address: String) {
        connectedTo.value = address
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("link", link.value)
            put("connected", connectedTo.value)
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("link")) {
            link.value = json["link"]!!.jsonPrimitive.content
        }
        if (json.containsKey("connected")) {
            connectedTo.value = json["connected"]!!.jsonPrimitive.content
        }
    }
}