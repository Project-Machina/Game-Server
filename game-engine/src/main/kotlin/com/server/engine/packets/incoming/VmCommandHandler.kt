package com.server.engine.packets.incoming

import com.server.engine.game.entity.character.components.VirtualMachineLinkComponent
import com.server.engine.game.entity.character.player.Player
import com.server.engine.game.entity.vms.commands.CommandManager
import com.server.engine.game.world.GameWorld
import com.server.engine.network.channel.packets.Packet
import com.server.engine.network.channel.packets.handlers.PacketHandler
import com.server.engine.network.session.NetworkSession
import com.server.engine.packets.message.VmCommandMessage
import com.server.engine.packets.outgoing.VmCommandOutput
import com.server.engine.utilities.inject
import com.server.engine.utilities.readSimpleString

class VmCommandHandler(override val opcode: Int = 2, val player: Player) : PacketHandler<VmCommandMessage, Unit> {

    val world: GameWorld by inject()

    override fun decode(packet: Packet, session: NetworkSession): VmCommandMessage {
        val command = packet.content.readSimpleString()
        val remote = packet.content.readBoolean()
        println("Decoding Command packet: $command - $remote")
        return VmCommandMessage(command, remote)
    }

    /**
     * Check if we are on the correct interface!
     */

    override fun handle(message: VmCommandMessage) {
        println("Handling $message")
        val link = player.component<VirtualMachineLinkComponent>()
        val vm = link.linkVM
        val command = message.command
        val rawArgs = command.split(' ')
        val name = rawArgs[0]
        val args = rawArgs.subList(1, rawArgs.size).toTypedArray()
        /*val output = CommandManager.execute(name, args, vm)
        if (output.isNotEmpty() || output.isNotBlank()) {
            player.session.sendMessage(VmCommandOutput(output, message.remote))
        }*/

    }
}