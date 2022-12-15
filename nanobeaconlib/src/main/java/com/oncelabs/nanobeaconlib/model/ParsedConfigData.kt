package com.oncelabs.nanobeaconlib.model

import com.oncelabs.nanobeaconlib.enums.DynamicDataType


data class ParsedConfigData (
    var advSetData : Array<ParsedAdvertisementData>,
)

data class ParsedAdvertisementData (
    var id : Int?,
    var bdAddr : String?,
    var parsedPayloadItems : ParsedPayload?,

    )

data class ParsedPayload (
    var deviceName : String? = null,
    var txPower : String? = null,
    var manufacturerData : List<ParsedDynamicData>?,
)

data class ParsedDynamicData (
    var len: Int,
    var dynamicType: DynamicDataType,
    var bigEndian : Boolean?,
    var encrypted : Boolean?,
)

