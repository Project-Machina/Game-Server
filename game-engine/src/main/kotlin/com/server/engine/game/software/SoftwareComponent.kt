package com.server.engine.game.software

import com.server.engine.game.components.Component
import com.server.engine.game.vms.VirtualMachine

interface SoftwareComponent : Component {

    val id: String

    suspend fun execute(vm : VirtualMachine) : Boolean

}