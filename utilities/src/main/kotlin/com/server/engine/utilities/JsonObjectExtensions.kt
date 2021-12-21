package com.server.engine.utilities

import kotlinx.serialization.json.*

fun JsonObject.string(key: String) : String {
    return this[key]?.jsonPrimitive?.content ?: ""
}

fun JsonObject.int(key: String) : Int {
    return this[key]?.jsonPrimitive?.int ?: 0
}

fun JsonObject.long(key: String) : Long {
    return this[key]?.jsonPrimitive?.long ?: 0L
}

fun JsonObject.boolean(key: String) : Boolean {
    return this[key]?.jsonPrimitive?.boolean ?: false
}

fun JsonObject.double(key: String) : Double {
    return this[key]?.jsonPrimitive?.double ?: 0.0
}

fun JsonObject.jobj(key: String) : JsonObject {
    return this[key]?.jsonObject ?: JsonObject(emptyMap())
}
