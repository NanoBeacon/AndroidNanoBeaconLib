package com.oncelabs.nanobeacon.model

enum class FilterType {
    NAME,
    ADDRESS,
    RSSI,
    HIDE_UNNAMED,
    ONLY_SHOW_CONFIGURATION,
    BY_TYPE;

    fun getName(): String {
        return when(this) {
            ADDRESS -> "address"
            RSSI -> "Minimum RSSI"
            HIDE_UNNAMED -> "Hide unnamed devices"
            ONLY_SHOW_CONFIGURATION -> "Only show project configuration matches"
            BY_TYPE -> "Type"
            NAME -> "name"
        }
    }

    fun getDescription(): String {
        return when(this) {
            NAME -> ""
            ADDRESS -> ""
            RSSI -> "dB"
            HIDE_UNNAMED -> "Hide unnamed"
            ONLY_SHOW_CONFIGURATION -> "Only show configurations"
            BY_TYPE -> ""
        }
    }

    fun getDefaultValue(): Any {
        return when(this) {
            RSSI -> -127f
            ADDRESS -> ""
            HIDE_UNNAMED -> false
            ONLY_SHOW_CONFIGURATION -> false
            BY_TYPE -> mutableMapOf(
                BeaconType.BEACON.description to false,
                BeaconType.EDDYSTONE.description to false,
            )
            NAME -> ""
        }
    }

    fun isEnabledByDefault(): Boolean {
        return when(this) {
            ADDRESS -> true
            RSSI -> true
            HIDE_UNNAMED -> false
            ONLY_SHOW_CONFIGURATION -> false
            BY_TYPE -> true
            NAME -> true
        }
    }

    fun getInputType(): FilterInputType {
        return when(this) {
            RSSI -> FilterInputType.SLIDER
            ADDRESS -> FilterInputType.SEARCH
            HIDE_UNNAMED -> FilterInputType.BINARY
            ONLY_SHOW_CONFIGURATION -> FilterInputType.BINARY
            BY_TYPE -> FilterInputType.OPTIONS
            NAME -> FilterInputType.SEARCH
        }
    }

    fun getRange(): Pair<Int, Int>? {
        return when(this) {
            RSSI -> Pair(-127, 0)
            ADDRESS -> null
            HIDE_UNNAMED -> null
            ONLY_SHOW_CONFIGURATION -> null
            BY_TYPE -> null
            NAME -> null
        }
    }
}