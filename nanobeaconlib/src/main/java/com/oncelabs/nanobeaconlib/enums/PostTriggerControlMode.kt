package com.oncelabs.nanobeaconlib.enums

enum class PostTriggerControlMode(val label : String, val trigerResetsCount : Boolean) {
    SINGLE("Single Trigger", false),
    SINGLE_RESET("Single Trigger", true),
    INDEFINITE("Indefinitely after 1rst", false),
    INDEFINITE_RESET("Indefinitely after 1rst", true),
    RECURRING("Recurring Trigger", false),
    RECURRING_RESET("Recuring Trigger", false);

    companion object  {
        fun fromCodes(code : Int?, trigCount : Int?) : PostTriggerControlMode? {
            when(code) {
                0 -> {
                    return if (trigCount == 0) {
                        INDEFINITE
                    } else {
                        SINGLE
                    }
                }
                1 -> {
                    return if (trigCount == 0) {
                        INDEFINITE_RESET
                    } else {
                        SINGLE_RESET
                    }
                }
                2 -> {
                    return RECURRING
                }
                3 -> {
                    return RECURRING_RESET
                }
                else -> {
                    return null
                }
            }
        }
    }
}