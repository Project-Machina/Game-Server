package com.server.engine.game.saving

import com.server.engine.game.vms.components.vevents.VirtualEvent
import com.server.engine.game.vms.components.vevents.VirtualEventsComponent
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class VirtalEventsSaving {

    @Test
    fun `virtual events saving`() {
        val comp = VirtualEventsComponent()

        comp.addEvent(VirtualEvent("test", "This is test event."))

        val format = Json { prettyPrint = true }

        val json = comp.save()
        val string = format.encodeToString(json)

        println(string)

        val newComp = VirtualEventsComponent()

        newComp.load(format.decodeFromString(string))

        val e = newComp.events[0]

        assert(e.source == "test")
        assert(e.message == "This is test event.")
        assert(e.hiddenVersion == 0.0)
        assert(e.id.startsWith("test:This is test event.")) { e.id }
    }

}