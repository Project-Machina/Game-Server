package com.server.engine.game.components

interface ComponentFactory<C : Component> {

    fun create() : C

}