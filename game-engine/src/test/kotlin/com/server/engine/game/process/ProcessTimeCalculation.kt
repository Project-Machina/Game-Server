package com.server.engine.game.process

import org.junit.jupiter.api.Test
import java.time.Duration

class ProcessTimeCalculation {

    @Test
    fun `time calc`() {

        val threads = 54
        val usage = 100

        val runningTime = 3000L
        val threadCost = 1

        val offset: Double = (threads.toDouble() / (usage + threadCost))

        println("$offset - $runningTime")

        val time: Long = if(offset > 1) {
            runningTime
        } else {
            (runningTime / offset).toLong()
        }

        println(time)
        println(Duration.ofMillis(time).toMinutes())

    }

}