package com.server.engine.game.entity.vms.software

import com.server.engine.game.components.dsl.ComponentDSL
import kotlinx.serialization.json.JsonObject

class SoftwareBuilder(private val name: String, private val extension: String, private val block : ComponentDSL<SoftwareComponent>.() -> Unit) {
    fun create() : VirtualSoftware {
        val soft = VirtualSoftware(name, extension)
        ComponentDSL(soft).block()
        return soft
    }
    companion object {
        fun software(name: String = "", ext: String = "", block: ComponentDSL<SoftwareComponent>.() -> Unit = {}) : VirtualSoftware {
            return SoftwareBuilder(name, ext, block).create()
        }
        fun fromJson(obj: JsonObject) : VirtualSoftware {
            val soft = software()
            soft.loadComponents(obj)
            return soft
        }
    }
}