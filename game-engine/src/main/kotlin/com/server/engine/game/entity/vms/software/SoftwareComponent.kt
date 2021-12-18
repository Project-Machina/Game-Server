package com.server.engine.game.entity.vms.software

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface SoftwareComponent : Component {

    val id: String

    val processBehaviour: VirtualProcessBehaviour
        get() = VirtualProcessBehaviour.NO_BEHAVIOUR
}