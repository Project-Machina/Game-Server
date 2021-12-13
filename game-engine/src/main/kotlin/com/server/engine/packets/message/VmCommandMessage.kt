package com.server.engine.packets.message

data class VmCommandMessage(val command: String, val remote: Boolean)