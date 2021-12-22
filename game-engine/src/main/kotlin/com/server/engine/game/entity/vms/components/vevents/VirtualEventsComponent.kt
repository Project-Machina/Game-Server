package com.server.engine.game.entity.vms.components.vevents

import com.server.engine.game.entity.vms.VMComponent
import kotlinx.serialization.json.*

class VirtualEventsComponent : VMComponent {

    val events = mutableMapOf<Int, VirtualEvent>()
    var isDirty: Boolean = false
        private set

    fun addEvent(event: VirtualEvent) {
        setEvent(getEventId(), event)
        isDirty = true
    }

    fun setEvent(id: Int, event: VirtualEvent) {
        event.eventId = id
        events[id] = event
        isDirty = true
    }

    fun clear() {
        events.clear()
        isDirty = true
    }

    fun remove(id: Int) {
        events.remove(id)
        isDirty = true
    }

    fun getEventId() : Int {
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
        } while(events.containsKey(eventId))
        return eventId
    }

    operator fun contains(id: Int) = events.contains(id)

    fun markClean() {
        isDirty = false
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            putJsonArray("events") {
                for (event in events.values) {
                    add(event.save())
                }
            }
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("events")) {
            val events = json["events"]!!.jsonArray
            for (event in events) {
                val e = VirtualEvent.create()
                e.load(event.jsonObject)
                addEvent(e)
            }
        }
    }
}