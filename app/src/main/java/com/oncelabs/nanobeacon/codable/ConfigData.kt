package com.oncelabs.nanobeacon.codable

data class ConfigData(
    val version : String? = null,
    val advSet : Array<AdvSetData>? = null,
    val vccUnit : Float? = null,
    val tempUnit : Float? = null
)

data class AdvSetData (
    val id : Int?= null,
    val bdAddr : String?= null,
    val addrType : String?= null,
    val advModeTrigEn : Int?= null,
    val payloadVer : Int?= null,
    val payload: Array<Payload>? = null,
    val gpioTrigerSrc : Array<Int>? =null,
    val postTrigCtrlMode : Int? = null,
    val postTrigNumAdv : Int? = null,
    val trigCheckPeriod : Int? = null
)

data class Payload (
    val len : Int? = null,
    val type : Int? = null,
    val data : String? = null
)


