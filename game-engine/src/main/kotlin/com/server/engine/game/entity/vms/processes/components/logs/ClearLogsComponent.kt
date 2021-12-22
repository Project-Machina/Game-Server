package com.server.engine.game.entity.vms.processes.components.logs

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent

class ClearLogsComponent(override var threadCost: Int) : OnFinishProcessComponent {

    override var networkCost: Int = 0
    override var ramCost: Long = 0
    override var runningTime: Long = 3000

    override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {
        if(source.has<VirtualEventsComponent>()) {
            val logs = source.component<VirtualEventsComponent>()
            logs.clear()
        }
    }
}