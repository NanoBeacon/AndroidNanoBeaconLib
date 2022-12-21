package com.oncelabs.nanobeacon.codable

data class ConfigData(
    val version : String? = null,
    val advSet : Array<AdvSetData>? = null,
    val vccUnit : Float? = null,
    val tempUnit : Float? = null,
    val txSetting : TxPower? = null
)

data class AdvSetData (
    val id : Int?= null,
    val bdAddr : String?= null,
    val interval : Int? = null,
    val addrType : String?= null,
    val advModeTrigEn : Int?= null,
    val payloadVer : Int?= null,
    val payload: Array<Payload>? = null,
    val gpioTrigerSrc : Array<Int>? =null,
    val postTrigCtrlMode : Int? = null,
    val postTrigNumAdv : Int? = null,
    val trigCheckPeriod : Int? = null,
    val ui_format : String
)

data class TxPower (
    val txPower : Int?,
    val sleepAftTx : Int?,
    val ch0 : Int?,
    val ch1 : Int?,
    val ch2 : Int?
)

data class Payload (
    val len : Int? = null,
    val type : Int? = null,
    val data : String? = null
)


