package com.oncelabs.nanobeacon.enum

enum class DigitalInputName(val abrv : String, val fullName : String) {
    INPUT("ie", "Input");

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