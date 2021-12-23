package com.server.engine.game.accounts

import com.server.engine.game.entity.vms.accounts.SystemAccountComponent
import com.server.engine.game.entity.vms.accounts.SystemAccountComponent.Companion.setAccount
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class SystemAccountTests {

    @Test
    fun `vm account test`() {
        val accman = SystemAccountComponent()
        val source = "1.1.1.1"

        assert(!accman.login(source, "test", "1")) { "Should not login, account test does not exist." }

        accman.setAccount("test", "2")

        assert(!accman.login(source, "test", "1")) { "Should not login, account password wrong." }

        assert(!accman.isActive(source)) { "Account is not active, haven't successfully logged in." }

        assert(accman.login(source, "test", "2")) { "Failed to login account test." }

        assert(accman.isGuest(source)) { "Failed to add default permission." }

        assert(!accman.isSSH(source)) { "Test shouldn't have ssh permission!" }

        assert(!accman.canExecuteSoftware(source)) { "Shouldn't be able to execute software!" }

        val account = accman.getActiveAccountFor(source)

        assert(account?.username == "test") { "Found wrong account for source." }

        val linkSource = "link"

        assert(accman.login(linkSource, "root", "")) { "source link should always be root" }

        assert(accman.isRoot(linkSource)) { "link source should be root" }

    }

    @Test
    fun `accounts saving test`() {
        val accman = SystemAccountComponent()
        val link = "link"

        val rootPass = accman.accounts["root"]!!.password

        accman.login(link)

        val json = Json { prettyPrint = true }

        val comps = accman.save()

        val save = json.encodeToString(comps)

        println(save)

        val accman2 = SystemAccountComponent()

        accman2.load(comps)

        assert(accman2.accounts.containsKey("root")) { println("Failed to load acc manager") }

        val root = accman2.accounts["root"]!!

        assert(root.password == rootPass) { "Failed to load accounts" }

    }

}