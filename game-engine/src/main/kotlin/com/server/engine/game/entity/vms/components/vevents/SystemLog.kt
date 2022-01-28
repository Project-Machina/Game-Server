package com.server.engine.game.entity.vms.components.vevents

import com.server.engine.game.components.ComponentFactory
import com.server.engine.utilities.double
import com.server.engine.utilities.int
import com.server.engine.utilities.long
import com.server.engine.utilities.string
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.time.ZoneOffset

class SystemLog(
    source: String,
    message: String,
    hiddenVersion: Double = 0.0,
    timestamp: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
) : EventComponent {

    var source: String = source
       private set
    var message: String = message
        private set
    var hiddenVersion: Double = hiddenVersion
        private set
    var timestamp: Long = timestamp
        private set

    var logId: Int = -1

    fun hide(version: Double) {
        this.hiddenVersion = version
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("logId", logId)
            put("source", source)
            put("message", message)
            put("hiddenVersion", hiddenVersion)
            put("timestamp", timestamp)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("logId")) {
            logId = json.int("logId")
        }
        if(json.containsKey("source")) {
            source = json.string("source")
        }
        if(json.containsKey("message")) {
            message = json.string("message")
        }
        if(json.containsKey("hiddenVersion")) {
            hiddenVersion = json.double("hiddenVersion")
        }
        if(json.containsKey("timestamp")) {
            timestamp = json.long("timestamp")
        }
    }


    companion object : ComponentFactory<SystemLog> {
        val NULL_LOG = create()
        override fun create(): SystemLog {
            return SystemLog("", "")
        }
    }

}