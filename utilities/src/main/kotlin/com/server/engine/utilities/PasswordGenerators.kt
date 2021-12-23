package com.server.engine.utilities

import kotlin.random.Random

private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val DIGITS = "0123456789"

fun generatePassword(length: Int = 16) : String {
    val builder = StringBuilder()
    repeat(length) {
        val char = when(Random.nextInt(3)) {
            1 -> LOWER.random()
            2 -> UPPER.random()
            else -> DIGITS.random()
        }
        builder.append(char)
    }
    return builder.toString()
}