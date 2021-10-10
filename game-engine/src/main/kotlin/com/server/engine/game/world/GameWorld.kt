package com.server.engine.game.world

import com.server.engine.game.inject
import com.server.engine.game.vms.VirtualMachine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

class GameWorld {

    private val format = Json { prettyPrint = true }
    private val dir = "C:\\Users\\david\\IdeaProjects\\ServerGameEngine\\world\\vms"

    val publicVirtualMachines = mutableMapOf<String, VirtualMachine>()

    val ip: InternetProtocolManager by inject()

    fun start() {
        loadWorld()
    }

    fun assignAddress(vm: VirtualMachine, address: String = "") {
        val addr = if(address.isEmpty() || address.isBlank()) {
            ip.reserveAddress()
        } else address
        publicVirtualMachines.putIfAbsent(addr, vm)
    }

    fun saveVirtualMachine(address: String) {
        val vm = publicVirtualMachines[address]
        if(vm != null) {
            val json = vm.saveComponents()
            Files.write(Path.of(dir, "$address.json"), format.encodeToString(json).toByteArray())
        }
    }

    fun loadVirtualMachine(address: String, reload: Boolean = false) {
        val file = Path.of(dir, "$address.json")
        if(file.exists()) {
            val addr = file.nameWithoutExtension
            val info = file.readText()
            val element = Json.parseToJsonElement(info)
            val virtualMachine = VirtualMachine()
            virtualMachine.loadComponents(element.jsonObject)
            if(reload) {
                publicVirtualMachines[addr] = virtualMachine
            } else {
                publicVirtualMachines.putIfAbsent(addr, virtualMachine)
            }
        }
    }

    fun saveWorld() {
        publicVirtualMachines.forEach { (addr, vm) ->
            Files.write(Path.of(dir, "$addr.json"), format.encodeToString(vm.saveComponents()).toByteArray())
        }
    }

    fun loadWorld() {
        val dir = Path.of("C:\\Users\\david\\IdeaProjects\\ServerGameEngine\\world\\vms").toFile()
        val vms = dir.listFiles()
        if(vms != null && vms.isNotEmpty()) {
            for (vm in vms) {
                if(vm.extension == "json") {
                    val addr = vm.nameWithoutExtension
                    val info = vm.readText()
                    val element = Json.parseToJsonElement(info)
                    val virtualMachine = VirtualMachine()
                    virtualMachine.loadComponents(element.jsonObject)
                    publicVirtualMachines[addr] = virtualMachine
                }
            }
        } else {
            println("No World data found.")
        }
    }

}