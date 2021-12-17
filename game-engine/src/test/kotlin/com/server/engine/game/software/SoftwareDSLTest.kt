package com.server.engine.game.software

import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.TextComponent
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
