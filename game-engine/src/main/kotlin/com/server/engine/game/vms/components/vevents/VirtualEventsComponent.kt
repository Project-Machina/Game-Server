package com.server.engine.game.vms.components.vevents

import com.server.engine.game.vms.VMComponent
import kotlinx.serialization.json.*
import java.util.*

class VirtualEventsComponent : VMComponent {

    val events = LinkedList<VirtualEvent>()

    fun addEvent(event: VirtualEvent) {
        events.addFirst(event)
        if(events.size >= 1000) {
            events.removeLast()
        }
    }

    fun clear() {
        events.clear()
    }

    fun remove(event: VirtualEvent) {
        remove(event.id)
    }

    fun remove(id: String) {
        events.removeIf { it.id == id }
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            putJsonArray("events") {
                for (event in events) {
                    add(event.save())
                }
            }
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("events")) {
            val events = json["events"]!!.jsonArray
            for (event in events) {
                addEvent(VirtualEvent.load(event.jsonObject))
            }
        }
    }
}