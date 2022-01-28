package com.server.engine.game.saving

import com.server.engine.game.entity.vms.components.vevents.SystemLog
import com.server.engine.game.entity.vms.components.vevents.SystemLogsComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class VirtalEventsSaving {

    @Test
    fun `virtual events saving`() {
        val comp = SystemLogsComponent()

        val event = SystemLog("test", "This is test event.")
        comp.addLog(event)

        val format = Json { prettyPrint = true }

        val json = comp.save()
        val string = format.encodeToString(json)

        println(string)

        val newComp = SystemLogsComponent()

        newComp.load(format.decodeFromString(string))

        val e = newComp.systemLogs.values.single()

        assert(e.source == "test")
        assert(e.message == "This is test event.")
        assert(e.hiddenVersion == 0.0)
    }

}