package com.oncelabs.nanobeacon.parser

import java.nio.ByteBuffer
import java.nio.ByteOrder

class DynamicDataParsers {
    companion object {
        fun processVcc(byteArray: ByteArray, vccUnit : Int, bigEndian : Boolean) : Int {
            var base = 0
            if (bigEndian) {
                val base = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).int
            } else {
                val base = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).int
            }
            return base * vccUnit
        }
    }
}