package com.server.engine.game.process

import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.ProcessComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ProcessTimeCalculation {

    @OptIn(ExperimentalTime::class, InternalCoroutinesApi::class)
    fun `time calc`(): Unit = runBlocking {

        val threads = Random.nextInt(2048)
        val processes = mutableListOf<VirtualProcess>()
        repeat(255) {
            val behs = mutableListOf<ProcessComponent>()
            repeat(Random.nextInt(10)) {
                behs.add(ProcessComponent.createAnonymous(Random.nextLong(60000), Random.nextInt(128)) { _, _ -> })
            }
            processes.add(VirtualProcess("test-$it"))
        }

        val threadUsage = Random.nextInt(2048)

        flow {
            repeat(20) {
                val time = measureTime {
                    repeat(50_000) {
                        for (process in processes) {
                            calculateRunningTime(process.minimalRunningTime, process.threadCost, threads, threadUsage)
                        }
                    }
                }
                emit(time)
                delay(300)
            }
        }.onEach {
            println(it)
        }.launchIn(this)

    }

    fun calculateRunningTime(defaultRunTime: Long = 3000L, threadCost: Int, threads: Int, threadUsage: Int): Long {
        val offset: Double = (threads.toDouble() / (threadUsage + threadCost))
        val extendedRunningTime = if (offset < 1) {
            (defaultRunTime / offset)
        } else defaultRunTime
        return extendedRunningTime.toLong()
    }

}