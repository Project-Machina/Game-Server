package com.server.engine.game.entity.vms.accounts

import com.server.engine.game.components.managers.SimpleComponentManager
import com.server.engine.utilities.string
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class Permission(name: String = "") : SimpleComponentManager() {

    var name: String = name
        private set

    override fun saveComponents(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("components", super.saveComponents())
        }
    }

    override fun loadComponents(json: JsonObject) {
        name = json.string("name")
        super.loadComponents(json["components"]!!.jsonObject)
    }
}