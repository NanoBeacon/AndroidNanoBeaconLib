package com.oncelabs.nanobeaconlib.enums

import java.util.*

enum class ConfigType(val label : String, val title : String){

    CUSTOM("Custom", "Custom"),
    EDDYSTONE("Eddystone", "Eddystone"),
    UID("UID", "Eddystone UID"),
    TLM("TLM", "Eddystone TLM"),
    IBEACON("iBeacon", "iBeacon"),
    NOT_RECOGNIZED("Not a recognized format", "Not a recognized format");

    companion object {
        fun fromLabel(label : String): ConfigType {
            return ConfigType.values().firstOrNull { it.label.lowercase() == label.lowercase() } ?: NOT_RECOGNIZED
        }
    }
}