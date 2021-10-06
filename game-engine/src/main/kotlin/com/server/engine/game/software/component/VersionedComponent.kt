package com.server.engine.game.software.component

import com.server.engine.game.software.SoftwareComponent

class VersionedComponent : SoftwareComponent {

    var version: Double = 1.0

    override suspend fun execute(tick: Long): Boolean {
        return true
    }
}