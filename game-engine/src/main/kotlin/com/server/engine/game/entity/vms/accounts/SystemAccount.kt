package com.server.engine.game.entity.vms.accounts

import com.server.engine.utilities.generatePassword

class SystemAccount(
    val username: String,
    password: String = generatePassword(),
    perms: List<String> = emptyList()
) {

    val permissions = mutableMapOf("guest" to true)

    var password: String = password
        private set

    init {
        for (perm in perms) {
            grant(perm)
        }
    }

    fun grant(perm: String) {
        permissions[perm] = true
    }

    fun newPassword(password: String = generatePassword()) {
        this.password = password
    }

    fun hasPerm(perm: String): Boolean {
        return permissions[perm] ?: false
    }

    fun hasPerms(vararg perms: String): Boolean {
        for (perm in perms) {
            if (!permissions.containsKey(perm))
                return false
        }
        return true
    }

    fun isRoot() = hasPerms("ftp", "ssh", "accman", "hidden", "guest")
    fun isFTP() = hasPerm("ftp")
    fun isSSH() = hasPerm("ssh")
    fun isAccountManager() = hasPerm("accman")
    fun isGuest() = hasPerm("guest")
    fun isHidden() = hasPerm("hidden")

    companion object {

        fun create(user: String, vararg perms: String): SystemAccount {
            val account = SystemAccount(user)
            for (perm in perms) {
                account.grant(perm)
            }
            return account
        }

    }

}