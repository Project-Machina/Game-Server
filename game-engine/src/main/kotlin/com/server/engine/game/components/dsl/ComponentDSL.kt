package com.server.engine.game.components.dsl

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager

class ComponentDSL<BASE : Component>(val manager: ComponentManager<BASE>) {

    infix fun add(comp: BASE) {
        manager.addComponent(comp)
    }

    inline fun <reified C : BASE> C.init(init: C.() -> Unit) {
        init(this)
        add(this)
    }

    inline infix fun<reified  C: BASE> ComponentFactory<C>.init(init: C.() -> Unit) {
        val comp = create()
        init(comp)
        add(comp)
    }

    operator fun BASE.unaryPlus() {
        manager.addComponent(this)
    }

    inline operator fun <reified C : BASE> ComponentFactory<C>.unaryPlus() {
        manager.addComponent(this.create())
    }

}