package com.server.engine.game.command

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import org.junit.jupiter.api.Test
import java.util.*

class VmCommandTest {

    @Test
    fun `testing vm commands`() {
        val command = "install -s cracker.crc -v 12.4"

        val args = command.split(" ")

        val name = args[0]
        val cmdArgs = args.subList(1, args.size)

        val parser = ArgParser(cmdArgs.toTypedArray())

        val softName: String by parser.storing("-s", "--s", help = "")
        val softVersion: Double by parser.storing("-v", "--v", help = "") { toDouble() }

        println(softName)
        println(softVersion)

    }

    @Test
    fun `testing spaced vm commands`() {
        val command = "install -s Cracker_Man.crc -v 12.4"

        val args = command.split(' ')

        val name = args[0]
        val cmdArgs = args.subList(1, args.size)

        println(cmdArgs)

        val parser = ArgParser(cmdArgs.toTypedArray())

        val softName: String by parser.storing("-s", "--s", help = "")
        val softVersion: Double by parser.storing("-v", "--v", help = "") { toDouble() }

        println(softName)
        println(softVersion)

    }

    @Test
    fun `testing more vm commands`() {
        val command = "install -s Cracker_Man.crc -v 12.4"

        val args = command.split(' ')

        val name = args[0]
        val cmdArgs = args.subList(1, args.size)

        println(cmdArgs)

        val parser = ArgParser(cmdArgs.toTypedArray())

        val softName by parser.storing("-s", "--s", help = "") { replace('_', ' ') }
        val someText: String by parser.storing("-e", "--e", help = "").default("")
        val softVersion: Double by parser.storing("-v", "--v", help = "") { toDouble() }
        val someNumber: Double by parser.storing("-V", "--V", help = "") { toDouble() }.default(0.0)

        println(softName)
        println(softVersion)
        println(someText)
        println(someNumber)

    }

    @Test
    fun `test adding parser`() {
        val command = "delete -i45 -i456 -i54 -i78"

        val args = command.split(' ')

        val name = args[0]
        val cmdArgs = args.subList(1, args.size)

        val parser = ArgParser(cmdArgs.toTypedArray())

        val nums by parser.adding("-i", help = "test")

        println(nums)

    }

    @Test
    fun `uuid test`() {
        val uuid = UUID.nameUUIDFromBytes(byteArrayOf())

        println(uuid.toString())

    }

}