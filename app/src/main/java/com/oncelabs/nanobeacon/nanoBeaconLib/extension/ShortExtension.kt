package com.oncelabs.nanobeacon.nanoBeaconLib.extension


fun Short.toHexString(): String {
    return "%02X".format(this)
}