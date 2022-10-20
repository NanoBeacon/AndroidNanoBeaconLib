package com.oncelabs.nanobeacon.model

enum class FilterType {
    RSSI;

    fun getName(): String {
        return "Minimum RSSI"
    }

    fun getDefaultValue(): Any {
        return when(this) {
            RSSI -> -127f
        }
    }

    fun getInputType(): FilterInputType {
        return when(this) {
            RSSI -> FilterInputType.SLIDER
        }
    }

    fun getRange(): Pair<Int, Int>? {
        when(this) {
            RSSI -> return Pair(-127, 0)
        }
    }
}