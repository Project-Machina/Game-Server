package com.server.engine.game.vms.components.hdd

import com.server.engine.game.software.VirtualSoftware
import com.server.engine.game.vms.UpgradableComponent
import com.server.engine.game.vms.VMComponent
import kotlinx.serialization.json.*

class FolderComponent(override val upgrades: UpgradableComponent = UpgradableComponent) : VMComponent {

    val softwares = mutableListOf<String>()

    fun moveSoftware(software: VirtualSoftware) : Boolean {
        return if(softwares.contains(software.id())) {
            false
        } else {
            softwares.add(software.id())
            true
        }
    }

    fun moveToParent(software: VirtualSoftware) {
        softwares.remove(software.id())
    }

    fun canDelete() : Boolean {
        return softwares.isEmpty()
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            putJsonArray("softwareInFolder") {
                softwares.forEach {
                    add(buildJsonObject { put("id", it) })
                }
            }
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("softwareInFolder")) {
            val array = json["softwareInFolder"]!!.jsonArray
            for (jsonElement in array) {
                val id = jsonElement.jsonObject["id"]!!.jsonPrimitive.content
                softwares.add(id)
            }
        }
    }
}