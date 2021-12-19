package com.server.engine.game.entity.vms.components.motherboard

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import kotlinx.serialization.json.*

class MotherboardComponent(
    var name: String = "",
    var cpuCapacity: Int = 1,
    var ramCapacity: Long = 128,
    var networkCardCapacity: Int = 10
) : VMComponent {

    val threadCapacity: Int get() = cpuCapacity * 2

    var availableThreads: Int = 2
    var availableRam: Long = 128
    var availableNetwork: Int = 10

    fun set(name: String, cpuCapacity: Int, ramCapacity: Long, networkCardCapacity: Int) {
        this.name = name
        this.cpuCapacity = cpuCapacity
        this.ramCapacity = ramCapacity
        this.networkCardCapacity = networkCardCapacity

        availableThreads = threadCapacity
        availableRam = ramCapacity
        availableNetwork = networkCardCapacity
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("cpu", cpuCapacity)
            put("ram", ramCapacity)
            put("networkCard", networkCardCapacity)
            put("usage", buildJsonObject {
                put("availableThreads", availableThreads)
                put("availableRam", availableRam)
                put("availableNetwork", availableNetwork)
            })
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("name")) {
            name = json["name"]!!.jsonPrimitive.content
        }
        if(json.containsKey("cpu")) {
            cpuCapacity = json["cpu"]!!.jsonPrimitive.int
        }
        if(json.containsKey("ram")) {
            ramCapacity = json["ram"]!!.jsonPrimitive.long
        }
        if(json.containsKey("networkCard")) {
            networkCardCapacity = json["networkCard"]!!.jsonPrimitive.int
        }
        if(json.containsKey("usage")) {
            val obj = json["usage"]!!.jsonObject
            if(obj.containsKey("availableThreads")) {
                availableThreads = obj["availableThreads"]!!.jsonPrimitive.int
            }
            if (obj.containsKey("availableRam")) {
                availableRam = obj["availableRam"]!!.jsonPrimitive.long
            }
            if(obj.containsKey("availableNetwork")) {
                availableNetwork = obj["availableNetwork"]!!.jsonPrimitive.int
            }
        }
    }

    companion object : ComponentFactory<MotherboardComponent> {
        override fun create(): MotherboardComponent {
            return MotherboardComponent()
        }
    }
}