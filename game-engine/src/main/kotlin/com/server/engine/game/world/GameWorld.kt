package com.server.engine.game.world

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.with
import com.server.engine.game.entity.vms.components.pages.HomePageComponent
import com.server.engine.utilities.get
import com.server.engine.utilities.inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

class GameWorld {

    private val format = Json { prettyPrint = true }
    private val dir = "C:\\Users\\david\\IdeaProjects\\ServerGameEngine\\world\\vms"

    val virtualMachines = mutableMapOf<UUID, VirtualMachine>()

    val publicVirtualMachines = mutableMapOf<String, VirtualMachine>()
    val vmToAddress = mutableMapOf<VirtualMachine, String>()
    val domainToAddress = mutableMapOf<String, String>()
    val addressToDomain = mutableMapOf<String, String>()

    val ip: InternetProtocolManager by inject()

    fun start() {
        loadWorld()
    }

    fun getVirtualMachine(address: String = "") : VirtualMachine {
        if(address.isEmpty())
            return publicVirtualMachines["1.1.1.1"]!!
        if(validateDomain(address))
            return publicVirtualMachines[domainToAddress[address]!!]!!
        return publicVirtualMachines[address]!!
    }

    fun validateDomain(domain: String) = domainToAddress.containsKey(domain)

    fun registerDomain(domain: String, address: String) {
        if(!validateDomain(domain) && publicVirtualMachines.containsKey(address)) {
            domainToAddress.putIfAbsent(domain, address)
            addressToDomain.putIfAbsent(address, domain)
        }
    }

    fun markPublic(vm: VirtualMachine, address: String = "") {
        val addr = if(address.isEmpty() || address.isBlank()) {
            ip.reserveAddress()
        } else address
        publicVirtualMachines.putIfAbsent(addr, vm)
        vmToAddress.putIfAbsent(vm, addr)
        virtualMachines[vm.id] = vm
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
            val elementObject = element.jsonObject
            val uuidString = elementObject["uuid"]!!.jsonPrimitive.content
            val virtualMachine = VirtualMachine.create(UUID.fromString(uuidString))
            virtualMachine.loadComponents(elementObject)
            virtualMachines[virtualMachine.id] = virtualMachine
            if(reload) {
                publicVirtualMachines[addr] = virtualMachine
                vmToAddress[virtualMachine] = addr
            } else {
                publicVirtualMachines.putIfAbsent(addr, virtualMachine)
                vmToAddress.putIfAbsent(virtualMachine, addr)
            }
        }
    }

    fun saveWorld() {
        publicVirtualMachines.forEach { (addr, vm) ->
            Files.write(Path.of(dir, "$addr.json"), format.encodeToString(vm.saveComponents()).toByteArray())
        }
    }

    fun loadTestWorld() {
        val testVM = VirtualMachine.create()
        val testIP = "74.97.118.97"
        testVM.name = "DrJavatar's VM"
        markPublic(testVM, testIP)

        val npcDefault = VirtualMachine.create()
        val defaultIP = "1.1.1.1"
        val domain = "First Whois.com"
        npcDefault.with(HomePageComponent("default"))
        markPublic(npcDefault, defaultIP)
        registerDomain(domain, defaultIP)

        val bankNpc = VirtualMachine.create()
        val bankIP = "1.2.3.4"
        val bankDomain = "Suite Bank.com"
        bankNpc.with(HomePageComponent("default-bank"))
        markPublic(bankNpc, bankIP)
        registerDomain(bankDomain, bankIP)
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
                    val elementObject = element.jsonObject
                    val uuidString = elementObject["uuid"]!!.jsonPrimitive.content
                    val virtualMachine = VirtualMachine.create(UUID.fromString(uuidString))
                    virtualMachine.loadComponents(elementObject)
                    virtualMachines[virtualMachine.id] = virtualMachine
                    publicVirtualMachines[addr] = virtualMachine
                    vmToAddress[virtualMachine] = addr
                }
            }
        } else {
            println("No World data found.")
        }
    }

    companion object {
        private val world: GameWorld = get()

        fun vmachine(uuid: UUID) : VirtualMachine? {
            return world.virtualMachines[uuid]
        }

        fun vmachine(uuid: String) : VirtualMachine? {
            return vmachine(UUID.fromString(uuid))
        }

    }

}