package com.oncelabs.nanobeacon.enums

import com.oncelabs.nanobeacon.model.BeaconType

enum class FilterType {
    NAME,
    ADDRESS,
    RSSI,
    HIDE_UNNAMED,
    ONLY_SHOW_CONFIGURATION,
    BY_TYPE,
    SORT_RSSI;

    /**
     * The name of the filter/sort option that is shown in the UI
     */
    fun getName(): String {
        return when(this) {
            ADDRESS -> "address"
            RSSI -> "Minimum RSSI"
            HIDE_UNNAMED -> "Hide unnamed devices"
            ONLY_SHOW_CONFIGURATION -> "Only show project configuration matches"
            BY_TYPE -> "Type"
            NAME -> "name"
            SORT_RSSI -> "Sort by RSSI"
        }
    }

    /**
     * The name of the filter/sort option that's shown in the filter preview when
     * it's active
     */
    fun getDescription(): String {
        return when(this) {
            NAME -> ""
            ADDRESS -> ""
            RSSI -> "dB"
            HIDE_UNNAMED -> "Hide unnamed"
            ONLY_SHOW_CONFIGURATION -> "Only show configurations"
            BY_TYPE -> ""
            SORT_RSSI -> "Sort RSSI"
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
            SORT_RSSI -> false
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
            SORT_RSSI -> false
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
            SORT_RSSI -> FilterInputType.BINARY
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
            SORT_RSSI -> null
        }
    }
}