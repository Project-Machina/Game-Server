package com.server.engine.game.address

import org.junit.jupiter.api.Test

class AddressParseTest {

    @Test
    fun `test address validation`() {

        val address = "54.48.111.255"
        val parts = address.split(".")

        assert(parts.all { it.toIntOrNull() != null && it.toInt() in 0..255 }) { "Failed to validate address." }

        val domain = "go\nogle.com"
        val domainParts = domain.split(".")

        val domainsToAddress = mutableMapOf<String, String>()

        domainsToAddress["google.com"] = "1.1.1.1"
        assert(!domainsToAddress.containsKey(domain)) { "Failed to find domain." }
        assert(domainParts[1] == "com" || domainParts[1] == "org" || domainParts[1] == "net") { "Failed to validate domain." }

    }

}