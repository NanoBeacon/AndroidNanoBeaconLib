package com.oncelabs.nanobeacon.enum

enum class ADType(val type : Int){

    EDDYSTONE_ADDRESS(3),
    DEVICE_NAME(9),
    TX_POWER(10),
    EDDYSTONE_DATA(22),
    MANUFACTURER_DATA(255);

    companion object {
        fun fromType(type : Int) : ADType? {
            return values().firstOrNull { it.type == type}
        }
    }
}