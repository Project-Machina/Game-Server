package com.server.engine.game.entity.vms.processes.behaviours

import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour

fun interface BehaviourFactory<T : VirtualProcessBehaviour> {

    fun create() : T

}