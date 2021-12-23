package com.server.engine.game.entity.vms.accounts

import com.server.engine.utilities.generatePassword

class SystemAccount(
    val username: String,
    password: String = generatePassword(),
    val permissions: MutableList<Permission> = mutableListOf(
        Permission("guest")
    )
) {

    var password: String = password
        private set

    fun newPassword(password: String = generatePassword()) {
        this.password = password
    }

    fun hasPerm(vararg perms: String): Boolean {
        return permissions.all { it.name in perms }
    }

    fun isRoot() = hasPerm("ftp", "ssh", "accman", "hidden", "guest")
    fun isFTP() = hasPerm("ftp")
    fun isSSH() = hasPerm("ssh")
    fun isAccountManager() = hasPerm("accman")
    fun isGuest() = hasPerm("guest")
    fun isHidden() = hasPerm("hidden")
}