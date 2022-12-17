package com.oncelabs.nanobeacon.enum

enum class ADType(val type : Int){

    DEVICE_NAME(9),
    TX_POWER(10),
    MANUFACTURER_DATA(255);

    companion object {
        fun fromType(type : Int) : ADType? {
            return values().firstOrNull { it.type == type}
        }
    }
}