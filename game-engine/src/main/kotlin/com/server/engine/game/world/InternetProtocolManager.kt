package com.server.engine.game.world

import java.util.concurrent.ThreadLocalRandom

class InternetProtocolManager {

    private val random = ThreadLocalRandom.current()

    val reservedAddresses = mutableListOf<String>()

    fun reserveAddress() : String {
        var address: String
        do {
            address = generateAddress()
        } while(reservedAddresses.contains(address))
        reservedAddresses.add(address)
        return address
    }

    private fun generateAddress() : String {
        val a1 = random.nextInt(255)
        val a2 = random.nextInt(255)
        val a3 = random.nextInt(255)
        val a4 = random.nextInt(255)
        return "$a1.$a2.$a3.$a4"
    }

}