package com.server.engine.utilities

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset

fun ByteBuf.readSimpleString(big: Boolean = false) : String {
    val length = if(big) readUnsignedShort() else readUnsignedByte().toInt()
    val charSeq = readCharSequence(length, Charset.forName("CP1252"))
    return charSeq.toString()
}

fun ByteBuf.readString(big: Boolean = false) : String {
    val length = if(big) readUnsignedShort() else readUnsignedByte().toInt()
    val charSeq = readCharSequence(length, Charset.defaultCharset())
    return charSeq.toString()
}

fun ByteBuf.writeSimpleString(value: String, big: Boolean = false) {
    if(big) {
        writeShort(value.length)
    } else {
        writeByte(value.length)
    }
    writeCharSequence(value, Charset.forName("CP1252"))
}

fun ByteBuf.writeString(value: String, big: Boolean = false) {
    if (big) {
        writeShort(value.length)
    } else {
        writeByte(value.length)
    }
    writeCharSequence(value, Charset.defaultCharset())
}