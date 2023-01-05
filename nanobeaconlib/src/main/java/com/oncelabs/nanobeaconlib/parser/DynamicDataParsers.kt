package com.oncelabs.nanobeaconlib.parser

import android.util.Log
import com.oncelabs.nanobeaconlib.extension.toHexString
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DynamicDataParsers {
    companion object {
        fun processVcc(byteArray: ByteArray, vccUnit : Float, bigEndian : Boolean) : Float? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }
            base?.let {
                return it * vccUnit
            } ?: run {
                return null
            }
        }

        fun processInternalTemp(byteArray: ByteArray, tempUnit : Float, bigEndian : Boolean) : Float? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }

            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }
            base?.let {
                return it * tempUnit
            } ?: run {
                return null
            }
        }

        fun processWireCount(byteArray: ByteArray, bigEndian : Boolean) : Int? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }

            return base
        }

        fun processGpioStatus(byteArray: ByteArray) : String? {
            var base : String? = null
            if (byteArray.isNotEmpty()) {
                base = byteArray[0].toUInt().toString(radix = 2)
                base = base.substring(base.length - 8, base.length)
            }

            return base
        }

        fun processGpioEdgeCount(byteArray: ByteArray, bigEndian : Boolean) : Int? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) + byteArray).order(byteOrder).int
            }
            return base
        }

        fun processCh01(byteArray: ByteArray, bigEndian : Boolean) : Int? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            if (byteArray.size == 2) {
                base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }

            return base
        }

        fun processTimeStamp(byteArray: ByteArray, bigEndian : Boolean, multiplier: Int = 1) : Int? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) + byteArray).order(byteOrder).int
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int
            }
            return base?.times(multiplier)
        }

        fun processAdv(byteArray: ByteArray, bigEndian : Boolean) : Int? {
            var base : Int? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) + byteArray).order(byteOrder).int
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int
            }
            return base
        }

        fun processRandomNumber(byteArray: ByteArray, bigEndian : Boolean) : UInt? {
            var base : UInt? = null
            val byteOrder = if (bigEndian) {
                ByteOrder.BIG_ENDIAN
            } else {
                ByteOrder.LITTLE_ENDIAN
            }
            when(byteArray.size) {
                1 -> base = byteArray[0].toUInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toUInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) + byteArray).order(byteOrder).int.toUInt()
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int.toUInt()
            }
            return base
        }

        fun processCustomerProductID(byteArray: ByteArray, bigEndian : Boolean) : String? {
            val hexString = byteArray.toHexString("")
            return if(bigEndian) { hexString.reversed()} else { hexString }
        }

        fun processBluetoothDeviceAddress(byteArray: ByteArray, bigEndian : Boolean) : String? {
            val hexString = byteArray.toHexString(":")
            return if(bigEndian) { byteArray.reversed().toByteArray().toHexString(":")} else { hexString }
        }

        fun processIBeaconUUID(byteArray: ByteArray) : String {
            return byteArray.toHexString("")
        }

        fun processMajor(byteArray: ByteArray) : String {
            return byteArray.toHexString("")
        }

        fun processMinor(byteArray: ByteArray) : String {
            return byteArray.toHexString("")
        }

        fun processIBeaconTxPower(byteArray: ByteArray) : Int {
            return byteArray[0].toInt()
        }

        fun processEddystoneNamespace(byteArray: ByteArray) : String {
            return byteArray.toHexString("")
        }

        fun processEddystoneInstance(byteArray: ByteArray) : String {
            return byteArray.toHexString("")
        }

    }
}