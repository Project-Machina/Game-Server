package com.server.engine.game.components

import kotlinx.serialization.json.JsonObject

interface Component {

    fun save() : JsonObject {
        return BLANK_JSON_OBJECT
    }
    fun load(json: JsonObject) {}

    companion object {
        val BLANK_JSON_OBJECT = JsonObject(emptyMap())
    }
}