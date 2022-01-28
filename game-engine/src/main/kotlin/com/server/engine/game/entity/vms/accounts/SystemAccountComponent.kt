package com.server.engine.game.entity.vms.accounts

import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.entity.vms.VMComponent
import com.server.engine.utilities.boolean
import com.server.engine.utilities.generatePassword
import com.server.engine.utilities.string
import kotlinx.serialization.json.*

class SystemAccountComponent : VMComponent {

    val accounts = mutableMapOf(
        "root" to SystemAccount("root", perms = listOf("ftp", "ssh", "accman", "guest"))
    )

    val activeAccounts = mutableMapOf<String, SystemAccount>()

    fun login(source: String, user: String = "", password: String = ""): Boolean {
        if (source == "link") {
            val root = accounts["root"]!!
            activeAccounts[source] = root
            return true
        } else if (accounts.containsKey(user)) {
            val account = accounts[user]!!
            if (account.password == password) {
                activeAccounts[source] = account
                return true
            }
        }
        return false
    }

    fun logout(source: String): Boolean {
        if (source == "link") {
            activeAccounts.remove(source)
            return true
        } else if (activeAccounts.containsKey(source)) {
            activeAccounts.remove(source)
            return true
        }
        return false
    }

    fun changePassword(source: String, user: String, password: String = generatePassword()): Boolean {
        if (activeAccounts.containsKey(source)) {
            val acc = activeAccounts[source]!!
            if (acc.username == user) {
                if (password.isEmpty()) {
                    acc.newPassword()
                } else acc.newPassword(password)
                return true
            } else if (acc.isAccountManager() || acc.isRoot()) {
                if (accounts.containsKey(user)) {
                    val account = accounts[user]!!
                    if (password.isEmpty()) {
                        account.newPassword()
                    } else account.newPassword(password)
                    return true
                }
            }
        }
        return false
    }

    fun getActiveAccountFor(source: String): SystemAccount? {
        return activeAccounts[source]
    }

    fun isRoot(source: String) = activeAccounts[source]?.isRoot() ?: false
    fun isFTP(source: String) = activeAccounts[source]?.isFTP() ?: false
    fun isSSH(source: String) = activeAccounts[source]?.isSSH() ?: false
    fun isGuest(source: String) = activeAccounts[source]?.isGuest() ?: false
    fun isHidden(source: String) = activeAccounts[source]?.isHidden() ?: false
    fun isActive(source: String) = activeAccounts.containsKey(source)

    fun canExecuteSoftware(source: String): Boolean {
        return isSSH(source) || isRoot(source)
    }

    fun canEditLogs(source: String) : Boolean {
        return !isGuest(source)
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            putJsonArray("accounts") {
                accounts.values.forEach {
                    add(buildJsonObject {
                        put("user", it.username)
                        put("pass", it.password)
                        putJsonArray("perms") {
                            it.permissions.forEach {
                                add(buildJsonObject {
                                    put("name", it.key)
                                    put("has", it.value)
                                })
                            }
                        }
                    })
                }
            }
        }
    }

    override fun load(json: JsonObject) {
        if (json.containsKey("accounts")) {
            val accountsArray = json["accounts"]!!.jsonArray
            accountsArray.map { it.jsonObject }.forEach { it ->
                val user = it.string("user")
                val pass = it.string("pass")
                val perms = mutableMapOf<String, Boolean>()
                if (it.containsKey("perms")) {
                    val permsArray = it["perms"]!!.jsonArray.map { it.jsonObject }
                    permsArray.forEach {
                        val name = it.string("name")
                        val has = it.boolean("has")
                        perms[name] = has
                    }
                }
                accounts[user] = SystemAccount(user, pass).also {
                    it.permissions.putAll(perms)
                }
            }
        }
    }

    companion object : ComponentFactory<SystemAccountComponent> {
        override fun create(): SystemAccountComponent {
            return SystemAccountComponent()
        }

        fun SystemAccountComponent.setAccount(user: String, password: String, perms: MutableList<Permission> = mutableListOf()) {
            val account = SystemAccount(user, password)
            accounts[user] = account
        }
    }
}