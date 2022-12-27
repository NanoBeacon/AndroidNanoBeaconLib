package com.oncelabs.nanobeacon.enum

enum class GlobalGpioTriggerName(val id : Int, val fullName : String) {
    GPIO0(0, "GPIO0"),
    GPIO1(1, "GPIO1"),
    GPIO2(2, "GPIO2"),
    GPIO3(3, "GPIO3"),
    MGPIO4(4, "MGPIO4"),
    MGPIO5(5, "MGPIO5"),
    MGPIO6(6, "MGPIO6"),
    MGPIO7(7, "MGPIO7");

    companion object {
        fun nameFromId(i : Int?) : String? {
            i?.let {
                val holder = values().firstOrNull { it.id == i }
                return holder?.fullName
            }
            return null
        }
    }

}