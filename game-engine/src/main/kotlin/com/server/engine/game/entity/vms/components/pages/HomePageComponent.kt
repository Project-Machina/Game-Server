package com.server.engine.game.entity.vms.components.pages

import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class HomePageComponent(path: String = "") : VMComponent {

    var path: String = path
        private set

    override fun save(): JsonObject {
        return buildJsonObject {
            put("path", path)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("name"))
            this.path = json.string("name")
    }
}