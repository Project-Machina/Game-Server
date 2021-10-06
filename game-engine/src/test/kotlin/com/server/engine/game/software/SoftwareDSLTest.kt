package com.server.engine.game.software

import com.server.engine.game.software.SoftwareBuilder.Companion.software
import com.server.engine.game.software.VirtualSoftware.Companion.has
import com.server.engine.game.software.component.TextComponent
import org.junit.jupiter.api.Test

class SoftwareDSLTest {

    @Test
    fun `DSL test`() {

        val soft = software("notes", "txt") {
            +TextComponent
        }

        assert(soft.has<TextComponent>()) { "Failed to add text component." }

    }

}
