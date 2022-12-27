package com.oncelabs.nanobeacon.enum

enum class WakeupName (val abrv : String, val fullName : String) {
    HIGH("high", "High"),
    LOW("low", "Low");

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