package com.server.engine.game.entity.vms.processes.components

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.processes.ProcessComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess

sealed interface ResourceComponent : ProcessComponent

class ResourceUsageComponent(
    override var threadCost: Int = 0,
    override var networkCost: Int = 0,
    override var ramCost: Long = 0,
    override var runningTime: Long = 0
) : ResourceComponent