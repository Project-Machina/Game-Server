package com.server.engine.game.software

import com.server.engine.game.components.Component

interface SoftwareComponent : Component {

    suspend fun execute(tick: Long) : Boolean

}