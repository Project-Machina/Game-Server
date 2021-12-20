package com.server.engine.game.software

import org.junit.jupiter.api.Test
import kotlin.math.pow

class SizeVersionTest {

    fun `test size calc on version comp`() {

        var version = 1.0
        val minSize = 10.0
        println(minSize / 3.4)
        repeat(5000) {

            val size = 10 + version.pow(2.94).toLong()

            val sizeGB = size / 1024
            val sizeTB = sizeGB / 1024
            val sizeEB = sizeTB / 1024

            println("Version ${String.format("%.1f", version)} - $size MB - $sizeGB GB - $sizeTB TB - $sizeEB EB")

            version += 0.1
        }

    }

    @Test
    fun `simple calc size`() {

        val size = calculateSizeByVersion(100.0)

        val GB = size / 1024
        val TB = GB / 1024
        val EB = TB / 1024

        println("$size MB - $GB GB - $TB TB - $EB EB")

    }

    @Test
    fun `max version`() {
        var version = 1.0

        do {
            val size = calculateSizeByVersion(version)
            version += 0.1
        } while(size > 0)

        println("Max Version: $version")
        println("If there are 100 different softwares")
        println("Max version for each software: ${version / 100}")
    }

    @Test
    fun `ram usage by size`() {
        val version = 888.0
        val size = calculateSizeByVersion(version)

        val ramUsage = size * 0.01

        println(size)
        println(ramUsage.toInt())

    }

    fun calculateSizeByVersion(version: Double) : Long {
        return 10 + version.pow(2.94).toLong()
    }


}