package com.server.engine.network.channel.login

import com.server.engine.network.session.NetworkSession

class LoginMessage(val username: String, val password: String, val session: NetworkSession)