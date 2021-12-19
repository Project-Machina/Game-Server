package com.server.engine.game.entity.vms.software

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour

interface SoftwareComponent : Component {

    val id: String

    val processBehaviour: VirtualProcessBehaviour
        get() = VirtualProcessBehaviour.NO_BEHAVIOUR

    val size: Long get() = 0
}