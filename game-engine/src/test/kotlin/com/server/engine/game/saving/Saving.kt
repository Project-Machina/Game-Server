package com.server.engine.game.saving

import com.server.engine.game.softCompsModule
import com.server.engine.game.software.SoftwareBuilder.Companion.software
import com.server.engine.game.software.VirtualSoftware.Companion.component
import com.server.engine.game.software.component.TextComponent
import com.server.engine.game.vmCompsModule
import com.server.engine.game.vms.VirtualMachine
import com.server.engine.game.vms.VirtualMachine.Companion.component
import com.server.engine.game.vms.VirtualMachine.Companion.has
import com.server.engine.game.vms.VirtualMachine.Companion.with
import com.server.engine.game.vms.components.hdd.HardDriveComponent
import com.server.engine.game.vms.components.power.PowerStorageComponent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class Saving {

    @Test
    fun `save via json`() {

        startKoin {
            modules(vmCompsModule, softCompsModule)
        }

        val format = Json { prettyPrint = true }

        val vm = VirtualMachine()
        val hdd = HardDriveComponent()

        vm.with(hdd)
        vm.with(PowerStorageComponent())

        val soft = software("Notes", "txt") {
            +TextComponent
        }

        val text = soft.component<TextComponent>()

        text.text = "Hello, World"

        hdd.addSoftware(soft)


        val json = vm.saveComponents()

        println(format.encodeToString(vm.saveComponents()))

        val newVM = VirtualMachine()

        newVM.loadComponents(json)

        assert(newVM.has<HardDriveComponent>()) { "Failed to load components" }

        val newHDD = newVM.component<HardDriveComponent>()

        newHDD.softwares.values.forEach {
            println(it.id())
        }

        val textFile = newHDD.getSoftwareByName("Notes.txt").single()

        assert(textFile.component<TextComponent>().text == "Hello, World") { "Failed to find software by name." }

        stopKoin()
    }

}