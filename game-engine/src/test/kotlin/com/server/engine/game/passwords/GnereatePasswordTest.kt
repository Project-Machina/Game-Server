package com.server.engine.game.passwords

import com.server.engine.utilities.generatePassword
import org.junit.jupiter.api.Test

class GnereatePasswordTest {

    @Test
    fun `gen pass`() {

        repeat(5) {
            val pass = generatePassword()
            println(pass)
        }

    }

}