package com.oncelabs.nanobeacon.parser

import android.util.Log
import com.oncelabs.nanobeaconlib.extension.toHexString
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DynamicDataParsers {
    companion object {
        fun processVcc(byteArray: ByteArray, vccUnit : Float, bigEndian : Boolean) : Float {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }

            return base * vccUnit
        }

        fun processInternalTemp(byteArray: ByteArray, tempUnit : Float, bigEndian : Boolean) : Float {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }

            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }
            Log.d("BASED",bigEndian.toString())
            return base * tempUnit
        }

        fun processWireCount(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }

            return base
        }

        fun processGpioStatus(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            if (byteArray.isNotEmpty()) {
                base = byteArray[0].toInt()
            }

            return base
        }

        fun processGpioEdgeCount(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) +  byteArray).order(byteOrder).int
            }

            return base
        }

        fun processCh01(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            if (byteArray.size == 2) {
                base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
            }
            return base
        }

        fun processTimeStamp(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) +  byteArray).order(byteOrder).int
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int
            }
            return base
        }

        fun processAdv(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) +  byteArray).order(byteOrder).int
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int
            }
            return base
        }

        fun processRandomNumber(byteArray: ByteArray, bigEndian : Boolean) : Int {
            var base = 0
            val byteOrder = if (bigEndian) { ByteOrder.BIG_ENDIAN } else { ByteOrder.LITTLE_ENDIAN }
            when(byteArray.size) {
                1 -> base = byteArray[0].toInt()
                2 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).short.toInt()
                3 -> base = ByteBuffer.wrap(byteArrayOf(0x00) +  byteArray).order(byteOrder).int
                4 -> base = ByteBuffer.wrap(byteArray).order(byteOrder).int
            }
            return base
        }

        fun processCustomerProductID(byteArray: ByteArray, bigEndian : Boolean) : String {
            val hexString = byteArray.toHexString("")
            return if(bigEndian) { hexString.reversed()} else { hexString }
        }

        fun processBluetoothDeviceAddress(byteArray: ByteArray, bigEndian : Boolean) : String {
            val hexString = byteArray.toHexString("")
            return if(bigEndian) { hexString.reversed()} else { hexString }
        }


    }
}