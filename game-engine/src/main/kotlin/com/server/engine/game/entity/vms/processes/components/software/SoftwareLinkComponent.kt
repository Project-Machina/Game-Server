package com.server.engine.game.entity.vms.processes.components.software

import com.server.engine.game.entity.vms.processes.ProcessComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware
import kotlinx.serialization.json.JsonObject

class SoftwareLinkComponent(
    var software: VirtualSoftware = VirtualSoftware.NULL_SOFTWARE,
    override var threadCost: Int = 0,
    override var networkCost: Int = 0,
    override var ramCost: Long = 0,
    override var runningTime: Long = 0
) : ProcessComponent {

    override fun save(): JsonObject {
        return software.saveComponents()
    }

    override fun load(json: JsonObject) {
        val soft = VirtualSoftware("", "")
        soft.loadComponents(json)
        software = soft
    }
}