package com.server.engine.game.components.dsl

import com.server.engine.game.components.Component
import com.server.engine.game.components.ComponentFactory
import com.server.engine.game.components.ComponentManager

class ComponentDSL<BASE : Component>(val manager: ComponentManager<BASE>) {

    operator fun BASE.unaryPlus() {
        manager.addComponent(this)
    }

    inline operator fun <reified C : BASE> ComponentFactory<C>.unaryPlus() {
        manager.addComponent(this.create())
    }

}