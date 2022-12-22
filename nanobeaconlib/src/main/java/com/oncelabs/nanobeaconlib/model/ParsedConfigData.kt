package com.oncelabs.nanobeaconlib.model

import com.oncelabs.nanobeaconlib.enums.*


data class ParsedConfigData (
    var advSetData : Array<ParsedAdvertisementData>,
    var vccUnit : Float,
    var tempUnit : Float,
    var txPower : Int?,
    var sleepAftTx : Boolean?,
    var ch0 : Int?,
    var ch1 : Int?,
    var ch2 : Int?,
    var globalTrigSettings : Map<SensorTriggerSource, GlobalTriggerSettings>?,
    var globalGpioTriggerSrc : Map<Int, GlobalGpio>?
)

data class ParsedAdvertisementData (
    var id : Int?,
    var bdAddr : String?,
    var ui_format : ConfigType,
    var parsedPayloadItems : ParsedPayload?,
    var interval : Int?,
    var chCtrl: Int,
    var advModeTrigEn : AdvMode?,
    var postTrigCtrlMode : PostTriggerControlMode?,
    var postTrigNumAdv : Int?,
    var trigCheckPeriod : Int?,
    var triggers : List<SensorTriggerSource>?,
    var gpioTriggers : List<Int>?,
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
    var rawData : String?
)

data class GlobalTriggerSettings (
    var threshold : Int,
    var src : String
)

data class GlobalGpio (
    var id : Int?,
    var digital : String?,
    var wakeup : String?,
    var advTrig : String?,
    var latch : Int?,
    var maskb : Int?
)