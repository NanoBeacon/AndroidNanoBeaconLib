package com.oncelabs.nanobeacon.model

import com.oncelabs.nanobeacon.codable.Payload
import com.oncelabs.nanobeacon.enum.ADType
import com.oncelabs.nanobeacon.enum.DynamicDataType

data class ParsedConfigData (
    var advSetData : Array<ParsedAdvertisementData>,
)

data class ParsedAdvertisementData (
    var id : Int?,
    var bdAddr : String?,
    var parsedPayloadItems : Array<ParsedPayload>?,

)

data class ParsedPayload (
    var adType: ADType,
    var deviceName : String?,
    var txPower : Int?,
    var manufacturerData : Array<ParsedDynamicData>?,
)

data class ParsedDynamicData (
    var len: Int,
    var dynamicType: DynamicDataType,
    var bigEndian : Boolean?,
    var encrypted : Boolean?,
)

