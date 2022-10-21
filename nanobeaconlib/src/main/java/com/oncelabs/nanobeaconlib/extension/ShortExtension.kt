package com.oncelabs.nanobeaconlib.extension


fun Short.toHexString(): String {
    return "%02X".format(this)
}