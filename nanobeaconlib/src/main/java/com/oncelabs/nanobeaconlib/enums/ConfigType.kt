package com.oncelabs.nanobeaconlib.enums

import java.util.*

enum class ConfigType(val label : String){

    CUSTOM("Custom"),
    EDDYSTONE("Eddystone"),
    UID("UID"),
    TLM("TLM"),
    IBEACON("iBeacon"),
    NOT_RECOGNIZED("Not a recognized format");

    companion object {
        fun fromLabel(label : String): ConfigType {
            return ConfigType.values().firstOrNull { it.label.lowercase() == label.lowercase() } ?: NOT_RECOGNIZED
        }
    }
}