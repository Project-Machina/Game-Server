package com.server.engine.packets.login

enum class LoginResponse {

    ACCEPTED,
    INVALID_USERNAME_OR_PASSWORD,
    BANNED,
    LOCKED,
    WORLD_FULL

}