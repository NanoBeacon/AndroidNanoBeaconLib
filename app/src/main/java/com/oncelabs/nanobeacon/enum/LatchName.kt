package com.oncelabs.nanobeacon.enum

enum class LatchName(val code : Int, val fullName : String) {
    DISABLED(0, "Disabled"),
    LATCH(1, "Latch"),
    LATCH_SLEEP(2, "Latch in sleep, wakeup unlatched");

    companion object {
        fun fromAbrv(ab : Int?) : String? {
            ab?.let {
                val holder = values().firstOrNull { it.code == ab }
                return holder?.fullName
            }
            return null
        }
    }
}