package com.oncelabs.nanobeaconlib.model

import com.oncelabs.nanobeaconlib.enums.AdvMode
import com.oncelabs.nanobeaconlib.enums.ConfigType
import com.oncelabs.nanobeaconlib.enums.DynamicDataType


data class ParsedConfigData (
    var advSetData : Array<ParsedAdvertisementData>,
    var vccUnit : Float,
    var tempUnit : Float,
    var txPower : Int?,
    var sleepAftTx : Boolean?,
    var ch0 : Int?,
    var ch1 : Int?,
    var ch2 : Int?
)

data class ParsedAdvertisementData (
    var id : Int?,
    var bdAddr : String?,
    var ui_format : ConfigType,
    var parsedPayloadItems : ParsedPayload?,
    var interval : Int?,
    var advModeTrigEn : AdvMode?,
    var chCtrl: Int
)

data class ParsedPayload (
    var deviceName : String? = null,
    var txPower : String? = null,
    var manufacturerData : Map<DynamicDataType, ParsedDynamicData>?,
)

data class ParsedDynamicData (
    var len: Int,
    var dynamicType: DynamicDataType,
    var bigEndian : Boolean?,
    var encrypted : Boolean?,
    var rawData : String?
)

