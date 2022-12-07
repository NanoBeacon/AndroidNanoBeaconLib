package com.oncelabs.nanobeacon.model

data class FilterOption(
    val filterType: FilterType,
    var enabled: Boolean = true,
    var value: Any?
) {

    companion object {
        fun getDefaultOptions(): List<FilterOption> {
            return FilterType.values().map {
                FilterOption(filterType = it, value = it.getDefaultValue(), enabled = it.isEnabledByDefault())
            }
        }
    }
}