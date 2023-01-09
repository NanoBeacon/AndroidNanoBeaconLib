package com.oncelabs.nanobeaconlib.model

import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType

data class ProcessedData (
       val dynamicDataType : DynamicDataType,
       val processedData : String,
       var bigEndian : Boolean?,
       var encrypted : Boolean?,
)

data class ProcessedDataAdv (
    val uiFormat : ConfigType,
    val processedData: List<ProcessedData>
)
