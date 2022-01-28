package com.server.engine.game.entity.vms.components.vevents

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.events.impl.SystemLogAlert
import com.server.engine.game.world.GameWorld
import com.server.engine.game.world.tick.VirtualMachineTick.Companion.VM_TICK_MILLIS
import kotlinx.serialization.json.*

class SystemLogsComponent : VMComponent {

    val systemLogs = mutableMapOf<Int, SystemLog>()
    private var isDirty: Boolean = false

    fun addLog(log: SystemLog) {
        if(log.logId == -1) {
            setLog(getLogId(), log)
        } else {
            setLog(log.logId, log)
        }
        isDirty = true
    }

    fun setLog(id: Int, log: SystemLog) {
        log.logId = id
        systemLogs[id] = log
        isDirty = true
    }

    fun clear() {
        systemLogs.clear()
        isDirty = true
    }

    fun remove(id: Int) {
        systemLogs.remove(id)
        isDirty = true
    }

    fun hideLog(logId: Int, version: Double) {
        val log = systemLogs[logId]
        if(log != null) {
            log.hide(version)
            isDirty = true
        }
    }

    fun getLogId() : Int {
        var eventId = 0
        var attempts  = 0
        do {
            if(attempts >= 2)
                break
            if(eventId >= 255) {
                eventId = 0
                attempts++
            }
            eventId++
        } while(systemLogs.containsKey(eventId))
        return eventId
    }

    operator fun contains(id: Int) = systemLogs.containsKey(id)

    private var lastUpdate: Long = 0

    override suspend fun onTick(source: VirtualMachine) {
        lastUpdate += VM_TICK_MILLIS
        if(isDirty || lastUpdate >= 600) {
            source.systemOutput.emit(SystemLogAlert(source, this))
            isDirty = false
            lastUpdate = 0
        }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            putJsonArray("events") {
                for (log in systemLogs.values) {
                    add(log.save())
                }
            }
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("events")) {
            val events = json["events"]!!.jsonArray
            for (event in events) {
                val e = SystemLog.create()
                e.load(event.jsonObject)
                addLog(e)
            }
        }
    }
}