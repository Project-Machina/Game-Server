package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.ProcessComponent

class RunSoftwareComponent(
    override var threadCost: Int,
    override var networkCost: Int,
    override var ramCost: Long,
    override var runningTime: Long
) : ProcessComponent {
    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {

    }
}