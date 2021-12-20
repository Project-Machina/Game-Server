package com.server.engine.game.entity.vms.components.vevents

import com.server.engine.game.entity.vms.VMComponent
import kotlinx.serialization.json.*

class VirtualEventsComponent : VMComponent {

    val events = mutableMapOf<Int, VirtualEvent>()

    fun addEvent(event: VirtualEvent) {
        setEvent(getEventId(), event)
    }

    fun setEvent(id: Int, event: VirtualEvent) {
        event.eventId = id
        events[id] = event
    }

    fun clear() {
        events.clear()
    }

    fun remove(id: Int) {
        events.remove(id)
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