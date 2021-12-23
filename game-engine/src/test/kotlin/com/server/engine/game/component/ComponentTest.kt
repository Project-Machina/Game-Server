package com.server.engine.game.component

import com.server.engine.game.components.Component
import com.server.engine.game.components.managers.SimpleComponentManager
import com.server.engine.game.components.managers.SimpleComponentManager.Companion.component
import com.server.engine.game.components.managers.SimpleComponentManager.Companion.has
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.singleton
import com.server.engine.game.entity.vms.processes.components.OnFinishProcessComponent
import com.server.engine.game.entity.vms.processes.components.software.InstallSoftwareComponent
import com.server.engine.game.etcModule
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.reflect.KClass

class ComponentTest {

    @Test
    fun `test class type keys`() {
        val m = SimpleComponentManager()

        m.addSingletonComponent(OnFinishComponent::class, CompleteProcess())

        assert(!m.has<CompleteProcess>()) { "Should not be able to find complete process class." }

        assert(m.has<OnFinishComponent>() && m.component<OnFinishComponent>() is CompleteProcess) { "Failed to find singleton class type" }

    }

    class CompleteProcess : OnFinishComponent

    interface OnFinishComponent : Component

    @Test
    fun `process singleton test`() {

        startKoin {
            modules(etcModule)
        }

        val pc = VirtualProcess("test")
        pc.singleton<OnFinishProcessComponent>(InstallSoftwareComponent())
        val t = pc.components.keys.single()
        assert(t.simpleName == "OnFinishProcessComponent")

        stopKoin()
    }

}