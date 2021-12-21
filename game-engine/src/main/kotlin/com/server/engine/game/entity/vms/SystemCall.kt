package com.server.engine.game.entity.vms

data class SystemCall(val name: String, val args: Array<String>, val isRemote: Boolean) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SystemCall

        if (name != other.name) return false
        if (!args.contentEquals(other.args)) return false
        if (isRemote != other.isRemote) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + isRemote.hashCode()
        return result
    }
}