package com.server.engine.game.world.tick

import com.server.engine.game.entity.character.Character
import kotlinx.coroutines.Job

class Subscription<C : Character>(val player: C, private val job: Job) : Job by job