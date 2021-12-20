package com.server.engine.game.entity.vms.components.vevents

import com.server.engine.game.components.ComponentFactory
import kotlinx.serialization.json.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset

class VirtualEvent(
    source: String,
    message: String,
    hiddenVersion: Double = 0.0,
    timestamp: Timestamp = Timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
) : EventComponent {

    var source: String = source
       private set
    var message: String = message
        private set
    var hiddenVersion: Double = hiddenVersion
        private set
    var timestamp: Timestamp = timestamp
        private set

    var eventId: Int = -1

    override fun save(): JsonObject {
        return buildJsonObject {
            put("source", source)
            put("message", message)
            put("hiddenVersion", hiddenVersion)
            put("timestamp", timestamp.time)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("source")) {
            source = json["source"]!!.jsonPrimitive.content
        }
        if(json.containsKey("message")) {
            message = json["message"]!!.jsonPrimitive.content
        }
        if(json.containsKey("hiddenVersion")) {
            hiddenVersion = json["hiddenVersion"]!!.jsonPrimitive.double
        }
        if(json.containsKey("timestamp")) {
            timestamp = Timestamp(json["timestamp"]!!.jsonPrimitive.long)
        }
    }


    companion object : ComponentFactory<VirtualEvent> {
        val NO_EVENT = create()
        override fun create(): VirtualEvent {
            return VirtualEvent("", "")
        }
    }

}