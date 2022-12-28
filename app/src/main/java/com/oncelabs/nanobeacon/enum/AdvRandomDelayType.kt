package com.oncelabs.nanobeacon.enum

enum class AdvRandomDelayType(val code : Int, val label : String) {
    ZERO(0, "0 ~ 10ms"),
    ONE(1, "0 ~ 20ms"),
    TWO(2, "0 ~ 80ms"),
    THREE(3, "0 ~ 160ms");

    companion object {
        fun fromCode(code : Int?) : String? {
            code?.let {
                return values().firstOrNull{it.code == code}?.label
            }
            return null
        }
    }

}