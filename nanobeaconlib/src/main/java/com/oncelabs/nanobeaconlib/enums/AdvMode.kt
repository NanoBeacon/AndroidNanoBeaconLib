package com.oncelabs.nanobeaconlib.enums

enum class AdvMode(val mode : Int, val label : String) {
    CONTINUOUS(0, "Continuous"),
    TRIGGERED(1, "Triggered");

    companion object {
        fun fromMode(mode : Int?) : AdvMode? {
            mode?.let {
                return values().firstOrNull{ it.mode == mode }
            } ?: run {
                return null
            }
        }
    }
}