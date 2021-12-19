package com.server.engine.game.entity.vms.components.hdd

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import kotlinx.serialization.json.*

class StorageRackComponent(
    var name: String = "",
    var maxCapacity: Long = 10000,
    var availableSpace: Long = 10000
) : VMComponent {

    override fun save(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("maxCapacity", maxCapacity)
            put("usedCapacity", availableSpace)
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("name")) {
            name = json["name"]!!.jsonPrimitive.content
        }
        if(json.containsKey("maxCapacity")) {
            maxCapacity = json["maxCapacity"]!!.jsonPrimitive.long
        }
        if(json.containsKey("usedCapacity")) {
            availableSpace = json["usedCapacity"]!!.jsonPrimitive.long
        }
    }

    companion object : ComponentFactory<StorageRackComponent> {
        override fun create(): StorageRackComponent {
            return StorageRackComponent()
        }
    }
}