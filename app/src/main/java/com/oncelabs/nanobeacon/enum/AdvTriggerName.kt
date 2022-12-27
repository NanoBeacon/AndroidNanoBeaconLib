package com.oncelabs.nanobeacon.enum

enum class AdvTriggerName(val abrv : String, val fullName : String) {
    LOW("low", "Low Level"),
    HIGH("high", "High Level"),
    RISING("rising","Rising Edge"),
    FALLING("falling", "Falling Edge");

    companion object {
        fun fromAbrv(ab : String?) : String? {
            ab?.let {
                val holder = values().firstOrNull { it.abrv == ab }
                return holder?.fullName
            }
            return null
        }
    }
}