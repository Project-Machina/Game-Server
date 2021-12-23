package com.server.engine.network.test

import org.junit.jupiter.api.Test
import java.io.File

class FileToNettyBuffer {

    fun `test size of npc file to netty packet`() {
        val file = File("/home/david/IdeaProjects/ServerGameEngine/world/assets/gameframe.fxml")
        //val text = file.readText(Charset.forName("CP1252"))
        println(file.readBytes().size)

    }

}