package com.server.engine.game.process

import com.server.engine.game.entity.vms.VirtualMachine
import com.server.engine.game.entity.vms.processes.VirtualProcess
import com.server.engine.game.entity.vms.processes.VirtualProcess.Companion.fromJson
import com.server.engine.game.entity.vms.processes.VirtualProcessBehaviour
import com.server.engine.game.entity.vms.processes.behaviours.BehaviourFactory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class ProcessSavingTest {

    @Test
    fun `test saving of behaviours`() {

        startKoin {
            modules(module {
                single<BehaviourFactory<out VirtualProcessBehaviour>>(named(TestBehaviour::class.simpleName!!)) { TestBehaviour }
            })
        }

        println(TestBehaviour::class.simpleName)

        val json = Json { prettyPrint = true }

        val pc = VirtualProcess("test", onFinishBehaviour = TestBehaviour("Hello, World"))

        val s = json.encodeToString(pc.toJson())

        println(s)

        val obj = json.decodeFromString<JsonElement>(s)

        val newPc = fromJson(obj.jsonObject)

        assert(newPc.onFinishBehaviour is TestBehaviour) { "Failed to load virtual process" }

        val test = newPc.onFinishBehaviour as TestBehaviour

        assert(test.networkCost == 1)
        assert(test.ramCost == 2L)
        assert(test.runningTime == 3L)
        assert(test.threadCost == 4)

        assert(test.msg == "Hello, World") { "Failed to load custom attributes." }

        stopKoin()

    }

    class TestBehaviour(var msg: String = "") : VirtualProcessBehaviour {
        override var networkCost: Int = 1
        override var ramCost: Long = 2
        override var runningTime: Long = 3
        override var threadCost: Int = 4

        override suspend fun onTick(source: VirtualMachine, process: VirtualProcess) {

        }

        override fun save(): JsonObject {
            return buildJsonObject {
                put("stats", super.save())
                put("msg", msg)
            }
        }

        override fun load(obj: JsonObject) {
            super.load(obj["stats"]!!.jsonObject)
            msg = obj["msg"]!!.jsonPrimitive.content
        }

        companion object : BehaviourFactory<TestBehaviour>{
            override fun create(): TestBehaviour {
                return TestBehaviour()
            }
        }
    }

}