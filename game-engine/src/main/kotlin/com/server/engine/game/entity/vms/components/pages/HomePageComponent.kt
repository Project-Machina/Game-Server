package com.server.engine.game.entity.vms.components.pages

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class HomePageComponent(name: String  = "default") : VMComponent {

    var name: String = name
        private set

    override fun save(): JsonObject {
        return buildJsonObject {
            put("name", name)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("name"))
            name = json.string("name")
    }
}