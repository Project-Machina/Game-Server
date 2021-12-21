package com.server.engine.game.entity.vms

import com.server.engine.dispatchers.VirtualMachineDispatcher
import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager
import com.server.engine.game.entity.TickingEntity
import com.server.engine.game.entity.vms.commands.CommandManager
import com.server.engine.game.entity.vms.components.NetworkCardComponent
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.entity.vms.components.hdd.HardDriveComponent
import com.server.engine.game.entity.vms.components.hdd.StorageRackComponent
import com.server.engine.game.entity.vms.components.motherboard.MotherboardComponent
import com.server.engine.game.entity.vms.components.vevents.VirtualEventsComponent
import com.server.engine.game.entity.vms.events.SystemOutput
import com.server.engine.game.entity.vms.processes.VirtualProcessComponent
import com.server.engine.utilities.get
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.*
import org.koin.core.qualifier.named
import java.util.*
import kotlin.reflect.KClass

class VirtualMachine private constructor(id: UUID = UUID.randomUUID()) : ComponentManager<VMComponent>, TickingEntity {

    private val _components = mutableMapOf<KClass<*>, VMComponent>()

    var id: UUID = id
        private set

    val components: Map<KClass<*>, VMComponent> get() = _components

    var name: String = "Virtual Machine"

    fun init() {
        with(ConnectionComponent())
        with(NetworkCardComponent())
        with(MotherboardComponent())
        with(StorageRackComponent())
        with(HardDriveComponent())
        with(CommandManager())
        with(VirtualEventsComponent())
        with(VirtualProcessComponent())

        systemCalls.onEach {
            if(has<CommandManager>()) {
                val manager = component<CommandManager>()
                if(it.isRemote && has<ConnectionComponent>()) {
                    val con = component<ConnectionComponent>()
                    if(con.remoteIP.value != "localhost") {
                        val remoteVM = con.remoteVM
                        manager.execute(it.args, this, remoteVM)
                    }
                } else {
                    manager.execute(it.args, this)
                }
            }
        }.launchIn(VirtualMachineDispatcher)
    }

    val systemCalls = MutableSharedFlow<SystemCall>(
        extraBufferCapacity = 255,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    val systemOutput = MutableSharedFlow<SystemOutput<*>>(
        extraBufferCapacity = Short.MAX_VALUE.toInt(),
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    override fun addComponent(component: VMComponent): ComponentManager<VMComponent> {
        _components.putIfAbsent(component::class, component)
        return this
    }

    override fun removeComponent(kclass: KClass<out VMComponent>): ComponentManager<VMComponent> {
        _components.remove(kclass)
        return this
    }

    override fun hasComponent(kclass: KClass<out VMComponent>): Boolean {
        return _components.containsKey(kclass)
    }

    override fun saveComponents(): JsonObject {
        return buildJsonObject {
            put("uuid", id.toString())
            putJsonArray("components") {
                components.values.forEach {
                    add(buildJsonObject {
                        put("compType", it::class.simpleName)
                        put("attributes", it.save())
                    })
                }
            }
        }
    }

    override fun loadComponents(json: JsonObject) {
        id = UUID.fromString(json["uuid"]!!.jsonPrimitive.content)
        if (json.containsKey("components")) {
            val comps = json["components"]?.jsonArray ?: JsonArray(emptyList())
            for (comp in comps) {
                val obj = comp.jsonObject
                if (obj.containsKey("compType") && obj.containsKey("attributes")) {
                    val type = obj["compType"]!!.jsonPrimitive.content
                    val compFactory: ComponentFactory<out VMComponent> = get(named(type))
                    val c = compFactory.create()
                    c.load(obj["attributes"]!!.jsonObject)
                    with(c)
                }
            }
        }
    }

    override suspend fun onTick() {
        components.values.forEach { it.onTick(this) }
    }

    companion object {
        inline fun <reified C : VMComponent> VirtualMachine.with(comp: C) = apply {
            addComponent(comp)
        }

        inline fun <reified C : VMComponent> VirtualMachine.has(): Boolean = hasComponent(C::class)
        inline fun <reified C : VMComponent> VirtualMachine.component(): C {
            return components[C::class] as C
        }

        fun create(uuid: UUID = UUID.randomUUID()): VirtualMachine {
            val vm = VirtualMachine(uuid)
            vm.init()
            return vm
        }

        val NULL_MACHINE = VirtualMachine(UUID.nameUUIDFromBytes(byteArrayOf(0)))

        @Deprecated(level = DeprecationLevel.WARNING, message = "This should only be use for unit tests.")
        fun unsafeCreate(): VirtualMachine {
            return VirtualMachine()
        }
    }

    override fun putComponent(component: VMComponent): ComponentManager<VMComponent> {
        _components[component::class] = component
        return this
    }

    override fun addSingletonComponent(
        kclass: KClass<out VMComponent>,
        component: VMComponent
    ): ComponentManager<VMComponent> {
        _components[kclass] = component
        return this
    }
}