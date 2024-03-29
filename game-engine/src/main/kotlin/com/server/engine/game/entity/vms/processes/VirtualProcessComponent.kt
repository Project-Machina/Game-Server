package com.server.engine.game.entity.vms.processes

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.components.NetworkCardComponent
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.events.impl.SystemProcessAlert
import com.server.engine.game.entity.vms.events.impl.SystemSoftwareAlert
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.component
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.has
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.SoftwareLinkComponent
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.ProcessOwnerComponent
import com.server.engine.game.world.tick.VirtualMachineTick
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

class VirtualProcessComponent : VMComponent {

    private val _activeProcesses = ConcurrentHashMap<Int, VirtualProcess>()
    val activeProcesses: Map<Int, VirtualProcess> = _activeProcesses
    val threadUsage: Int get() = activeProcesses.values.sumOf { it.threadCost }
    val ramUsage: Long get() = activeProcesses.values.sumOf { it.ramCost }
    val networkUsage: Int get() = activeProcesses.values.count { it.usesNetwork }

    fun calculateNetworkTime(size: Long, networkSpeed: Int, defaultTime: Long = 3000L) : Long {
        val megabits = networkSpeed / networkUsage
        val megabytes = (megabits / 8)
        val runningTime = size / megabytes
        if(runningTime < defaultTime) {
            return defaultTime
        }
        return runningTime
    }

    fun calculateRunningTime(defaultRunTime: Long = 3000L, cost: Int, available: Int, usage: Int): Long {
        val offset: Double = (available.toDouble() / (usage + cost))
        val extendedRunningTime = if (offset < 1) {
            (defaultRunTime / offset)
        } else defaultRunTime
        return extendedRunningTime.toLong()
    }

    fun addProcess(process: VirtualProcess): Int {
        val pid = getProcessId()
        if (pid > 0) {
            process.pid = pid
            _activeProcesses[pid] = process
        }
        return pid
    }

    private fun getProcessId(): Int {
        var pid = 0
        var attempts = 0
        do {
            if (attempts >= 2)
                break
            if (pid > 255) {
                attempts++
                pid = 0
            }
            pid++
        } while (activeProcesses.containsKey(pid))
        return pid
    }

    override suspend fun onTick(source: VirtualMachine) {
        val iter = _activeProcesses.iterator()
        val mb = source.component<MotherboardComponent>()
        val conn = source.component<ConnectionComponent>()
        while (iter.hasNext()) {
            val set = iter.next()
            val pc = set.value

            if (pc.isKilled) {
                iter.remove()
                if (pc.has<SoftwareLinkComponent>()) {
                    val software = pc.component<SoftwareLinkComponent>().software
                    if (software.has<ProcessOwnerComponent>()) {
                        software.component<ProcessOwnerComponent>().pid = -1
                        source.systemOutput.emit(SystemSoftwareAlert(source))
                    }
                }
                source.systemOutput.emit(SystemProcessAlert(source, pc))
                continue
            }

            if (pc.immediate) {
                pc.components.values.filter { it !is OnFinishProcessComponent }
                    .forEach {
                        it.onTick(source, pc)
                    }
                if (pc.has<OnFinishProcessComponent>()) {
                    pc.component<OnFinishProcessComponent>().onTick(source, pc)
                }
                iter.remove()
            } else {

                if (!pc.isPaused && !pc.isComplete) {
                    if (!pc.isIndeterminate) {
                        pc.elapsedTime += VirtualMachineTick.VM_TICK_MILLIS
                        if(pc.usesNetwork) {
                            if(conn.remoteVM == null) {
                                pc.isKilled = true
                                continue
                            }
                            val soft = pc.component<SoftwareLinkComponent>().software
                            val target = conn.remoteVM!!
                            val targetMb = target.component<MotherboardComponent>()
                            val availableNetwork = (mb.availableNetwork + targetMb.availableNetwork) / 2
                            pc.preferredRunningTime = calculateNetworkTime(soft.size, availableNetwork)
                        } else {
                            pc.preferredRunningTime = calculateRunningTime(
                                pc.minimalRunningTime,
                                pc.threadCost,
                                mb.availableThreads,
                                threadUsage
                            )
                        }
                    }
                    pc.components.values.filter { it !is OnFinishProcessComponent }.forEach { it.onTick(source, pc) }
                }
                if (pc.isComplete && pc.shouldComplete) {
                    if (pc.has<OnFinishProcessComponent>()) {
                        pc.component<OnFinishProcessComponent>().onTick(source, pc)
                    }
                    iter.remove()
                }
                source.systemOutput.emit(SystemProcessAlert(source, pc))
            }
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("processes", buildJsonArray {
                activeProcesses.values.forEach {
                    add(it.saveComponents())
                }
            })
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("processes")) {
            val processArray = json["processes"]!!.jsonArray
            processArray.forEach {
                val pc = VirtualProcess("")
                pc.loadComponents(it.jsonObject)
                addProcess(pc)
            }
        }
    }
}