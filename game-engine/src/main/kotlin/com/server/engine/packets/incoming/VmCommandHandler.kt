package com.server.engine.packets.incoming

import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.components.WidgetManagerComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.VirtualMachine.Companion.component
import com.server.engine.game.entity.vms.VirtualMachine.Companion.has
import com.server.engine.game.entity.vms.commands.CommandManager
import com.server.engine.game.entity.vms.components.connection.ConnectionComponent
import com.server.engine.game.world.GameWorld
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.message.VmCommandMessage
import com.server.engine.utilities.inject
import com.server.engine.utilities.readSimpleString

class VmCommandHandler(override val opcode: Int = 2, val player: Player) : PacketHandler<VmCommandMessage, Unit> {

    val world: GameWorld by inject()

    val commandWhiteList = mutableMapOf(
        "spawn" to listOf("javatar")
    )

    override fun decode(packet: Packet, session: NetworkSession): VmCommandMessage {
        val command = packet.content.readSimpleString()
        val remote = packet.content.readBoolean()
        return VmCommandMessage(command, remote)
    }

    /**
     * Check if we are on the correct interface!
     */

    override fun handle(message: VmCommandMessage) {
        val link = player.component<VirtualMachineLinkComponent>()
        val vm = link.linkVM
        val command = message.command
        val rawArgs = command.split(' ').toTypedArray()
        val wm = player.component<WidgetManagerComponent>()

        if(commandWhiteList.containsKey(rawArgs[0]) && player.name.lowercase() !in (commandWhiteList[rawArgs[0]] ?: emptyList())) {
            return
        }

        try {
            if(vm.has<CommandManager>()) {
                val manager = vm.component<CommandManager>()
                if(message.remote && wm.currentWidget.value == "internet" && vm.has<ConnectionComponent>()) {
                    val con = vm.component<ConnectionComponent>()
                    if(con.remoteIP.value != "localhost") {
                        val remoteVM = con.remoteVM
                        manager.execute(rawArgs, vm, remoteVM)
                    }
                } else {
                    manager.execute(rawArgs, vm, vm)
                }
            } else {
                println("Player does not have manager ${player.name}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}