package com.oncelabs.nanobeacon.model

import com.oncelabs.nanobeacon.enums.FilterInputType
import com.oncelabs.nanobeacon.enums.FilterType
import kotlin.math.roundToInt

data class FilterOption(
    val filterType: FilterType,
    var enabled: Boolean = true,
    var value: Any?
) {

    fun getDescription(): String? {
        // Check if filter is active
        enabled.takeIf { it } ?: return null

        val isActive: Boolean = when(filterType) {
            FilterType.NAME -> {
                ((value as? String)?.isNotEmpty() == true)
            }
            FilterType.ADDRESS -> {
                ((value as? String)?.isNotEmpty() == true)
            }
            FilterType.RSSI -> {
                ((value as? Float)?.roundToInt() ?: -127) > -127
            }
            FilterType.HIDE_UNNAMED -> {
                enabled
            }
            FilterType.ONLY_SHOW_CONFIGURATION -> {
                enabled
            }
            FilterType.BY_TYPE -> {
                (value as? MutableMap<String, Boolean>)?.values?.any { it } ?: false
            }
            FilterType.SORT_RSSI -> {
                enabled
            }
            FilterType.ADVANCED_SEARCH -> {
                ((value as? String)?.isNotEmpty() == true)
            }
        }
        return if (isActive) {
            when(filterType.getInputType()) {
                FilterInputType.BINARY -> filterType.getDescription()
                FilterInputType.SLIDER, FilterInputType.OPTIONS -> "${(value as? Float)?.roundToInt()} ${filterType.getDescription()}"
                FilterInputType.SEARCH -> "$value"
            }
        } else {
            null
        }
    }

    companion object {
        fun getDefaultOptions(): List<FilterOption> {
            return FilterType.values().map {
                FilterOption(filterType = it, value = it.getDefaultValue(), enabled = it.isEnabledByDefault())
            }
        }
    }
}