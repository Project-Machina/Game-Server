package com.server.engine.game.entity.vms.components.hdd

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.UpgradableComponent
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.components.power.PoweredComponent
import com.server.engine.game.entity.vms.events.impl.VirtualSoftwareUpdateEvent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.has
import com.server.engine.game.entity.vms.software.component.VersionedComponent
import com.server.engine.game.entity.vms.upgrades.HardDriveUpgradeComponent
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*

class HardDriveComponent(override val upgrades: UpgradableComponent = HardDriveUpgradeComponent()) :
    VMComponent {

    val softwares = mutableMapOf<String, VirtualSoftware>()
    val folders = mutableMapOf<String, FolderComponent>()

    val driveUsage: Long get() = softwares.values.sumOf { it.size }

    fun addSoftware(software: VirtualSoftware) : Boolean {
        return if(softwares.containsKey(software.id())) {
            true
        } else {
            softwares.putIfAbsent(software.id(), software)
            false
        }
    }

    fun getSoftware(id: String) : VirtualSoftware {
        return softwares[id] ?: error("Software does not exist.")
    }

    fun hasSoftware(id: String) : Boolean {
        return softwares.containsKey(id)
    }

    fun getBestSoftware(extension: String) : VirtualSoftware? {
        return softwares.values
            .filter { it.extension == extension && it.has<VersionedComponent>() }
            .maxByOrNull { it.component<VersionedComponent>().version }
    }

    fun getWeakestSoftware(extension: String) : VirtualSoftware? {
        return softwares.values
            .filter { it.extension == extension && it.has<VersionedComponent>() }
            .minByOrNull { it.component<VersionedComponent>().version }
    }

    fun getSoftwaresInFolder(folderName: String) : List<VirtualSoftware> {
        val folder = this.folders[folderName]
        if(folder != null) {
            return folder.softwares.filter { softwares.containsKey(it) }.map { softwares[it]!! }
        }
        return emptyList()
    }

    fun getSoftwaresByName(name: String) : List<VirtualSoftware> {
        val list = mutableListOf<VirtualSoftware>()
        for (value in softwares.values) {
            if(value.fullName == name) {
                list.add(value)
            }
        }
        return list
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("upgrades", upgrades.save())
            if (powerConsumption !== PoweredComponent.NO_POWER) {
                put("power_used", powerConsumption.save())
            }
            putJsonArray("softwares") {
                softwares.values.forEach {
                    add(it.saveComponents())
                }
            }
            put("folders", buildJsonArray {
                folders.forEach { (name, comp) ->
                    add(buildJsonObject {
                        put("folderName", name)
                        put("softwares", comp.save())
                    })
                }
            })
        }
    }

    override fun load(json: JsonObject) {
        if(json.containsKey("upgrades")) {
            upgrades.load(json["upgrades"]!!.jsonObject)
        }
        if(json.containsKey("power_used")) {
            powerConsumption.load(json["power_used"]!!.jsonObject)
        }
        if(json.containsKey("softwares")) {
            val comps = json["softwares"]?.jsonArray ?: JsonArray(emptyList())
            for (comp in comps) {
                val software = comp.jsonObject
                if(software.containsKey("name")) {
                    val soft = software()
                    soft.loadComponents(software)
                    addSoftware(soft)
                }
            }
        }
        if(json.containsKey("folders")) {
            val folders = json["folders"]!!.jsonArray
            for (folder in folders) {
                val obj = folder.jsonObject
                val name = obj["folderName"]!!.jsonPrimitive.content
                if(obj.containsKey("softwares")) {
                    val fc = FolderComponent()
                    fc.load(obj["softwares"]!!.jsonObject)
                    this.folders[name] = fc
                } else {
                    this.folders[name] = FolderComponent()
                }
            }
        }
    }

    override suspend fun onTick(source: VirtualMachine) {
        softwares.values.forEach {
            source.updateEvents.emit(VirtualSoftwareUpdateEvent(source, it))
        }
        delay(1000)
    }

    companion object : ComponentFactory<HardDriveComponent> {
        override fun create(): HardDriveComponent {
            return HardDriveComponent()
        }
    }
}