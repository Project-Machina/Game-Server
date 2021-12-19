package com.server.engine.game.software

import com.server.engine.game.entity.vms.software.SoftwareBuilder
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import org.junit.jupiter.api.Test
import java.util.*

class HashCodeAsIDTest {

    @Test
    fun `software hashing`() {

        val cracker = SoftwareBuilder.software("cracker", "crc") {
            +VersionedComponent
        }
        cracker.component<VersionedComponent>().version = 45.4

        val cracker1 = SoftwareBuilder.software("cracker", "crc") {
            +VersionedComponent
        }
        cracker1.component<VersionedComponent>().version = 45.4

        val crackerHash = UUID.nameUUIDFromBytes(cracker.id().toByteArray())
        val cracker1Hash = UUID.nameUUIDFromBytes(cracker1.id().toByteArray())

        println(crackerHash)
        println(cracker1Hash)

        assert(crackerHash == cracker1Hash) { "IDs are different $crackerHash $cracker1Hash" }

    }
}