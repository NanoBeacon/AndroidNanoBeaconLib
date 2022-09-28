package com.oncelabs.template.nanoBeaconLib.extension

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.toShort(start: Int = 0, endian: ByteOrder = ByteOrder.BIG_ENDIAN): Short {
    this.size.takeIf { it >= 2 } ?: return 0
    return ByteBuffer.wrap(this.copyOfRange(start, start + 2)).order(endian).short
}

fun ByteArray.toInt(start: Int = 0, endian: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
    this.size.takeIf { it >= 4 } ?: return 0
    return ByteBuffer.wrap(this.copyOfRange(start, start + 4)).order(endian).int
}