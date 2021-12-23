package com.server.engine.game.saving

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.VirtualMachine.Companion.with
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.power.PowerStorageComponent
import com.server.engine.game.entity.vms.software.SoftwareBuilder.Companion.software
import com.server.engine.game.entity.vms.software.VirtualSoftware.Companion.component
import com.server.engine.game.entity.vms.software.component.TextComponent
import com.server.engine.game.softCompsModule
import com.server.engine.game.vmCompsModule
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

        val vm = VirtualMachine.unsafeCreate()
        val hdd = HardDriveComponent()

        vm.with(hdd)

        val soft = software("Notes", "txt") {
            +TextComponent
        }

        val text = soft.component<TextComponent>()

        text.text = "Hello, World"

        hdd.addSoftware(soft)


        val json = vm.saveComponents()

        println(format.encodeToString(vm.saveComponents()))

        val newVM = com.server.engine.game.entity.vms.VirtualMachine.unsafeCreate()

        newVM.loadComponents(json)

        assert(newVM.has<HardDriveComponent>()) { "Failed to load components" }

        val newHDD = newVM.component<HardDriveComponent>()

        newHDD.softwares.values.forEach {
            println(it.id())
        }

        val textFile = newHDD.getSoftwaresByName("Notes.txt").single()

        assert(textFile.component<TextComponent>().text == "Hello, World") { "Failed to find software by name." }

        stopKoin()
    }

}