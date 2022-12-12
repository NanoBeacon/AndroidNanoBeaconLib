package com.oncelabs.nanobeacon.model

import com.oncelabs.nanobeacon.enums.FilterInputType
import com.oncelabs.nanobeacon.enums.FilterType

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
                ((value as? Float) ?: -127.0f) > -127.0f
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
        }
        return if (isActive) {
            when(filterType.getInputType()) {
                FilterInputType.BINARY -> filterType.getDescription()
                FilterInputType.SLIDER, FilterInputType.OPTIONS -> "$value ${filterType.getDescription()}"
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