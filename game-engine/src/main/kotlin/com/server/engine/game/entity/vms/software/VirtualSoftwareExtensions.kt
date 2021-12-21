package com.server.engine.game.entity.vms.software

import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.entity.vms.software.component.VisibleComponent

fun VirtualSoftware.isRunning() : Boolean {
    return has<ProcessOwnerComponent>() && component<ProcessOwnerComponent>().pid != -1
}

fun VirtualSoftware.isHidden() : Boolean {
    return has<VisibleComponent>() && component<VisibleComponent>().hiddenVersion > 0.0
}