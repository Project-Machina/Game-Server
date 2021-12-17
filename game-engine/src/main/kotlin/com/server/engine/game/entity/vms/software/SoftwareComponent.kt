package com.server.engine.game.entity.vms.software

import com.server.engine.game.components.Component
import com.server.engine.game.entity.vms.VirtualMachine

interface SoftwareComponent : Component {

    val id: String

    suspend fun run(source: VirtualMachine, target: VirtualMachine = source) : Boolean

    suspend fun install(source: VirtualMachine, target: VirtualMachine = source) {

    }

    suspend fun uninstall(source: VirtualMachine, target: VirtualMachine = source) {

    }

}