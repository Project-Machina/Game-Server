package com.server.engine.game.entity.vms.software

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.processes.ProcessComponent

interface SoftwareComponent : Component {

    val id: String

    val size: Long get() = 0
}