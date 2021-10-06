package com.server.engine.utilities

import java.io.FileOutputStream
import java.nio.file.Path
import java.security.KeyPairGenerator
import java.util.*

object RSAKeyGenerator {

    private val pair = KeyPairGenerator.getInstance("RSA")
    fun generate() {
        pair.initialize(1024)

        val pair = this.pair.genKeyPair()

        val publicKey = pair.public
        val privateKey = pair.private

        writeToFile(
            Path.of("C:\\Users\\david\\IdeaProjects\\ServerGameEngine\\keys\\public.txt"),
            Base64.getEncoder().encodeToString(publicKey.encoded)
        )
        writeToFile(
            Path.of("C:\\Users\\david\\IdeaProjects\\ServerGameEngine\\keys\\private.txt"),
            Base64.getEncoder().encodeToString(privateKey.encoded)
        )
    }

    fun writeToFile(path: Path, bytes: String) {
        val file = path.toFile()
        file.parentFile.mkdirs()
        val out = FileOutputStream(file)
        out.write(bytes.toByteArray())
        out.flush()
        out.close()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        generate()
    }

}