package com.oncelabs.nanobeacon.codable

data class ConfigData(
    val version : String? = null,
    val advSet : Array<AdvSetData>? = null,
    val vccUnit : Float? = null,
    val tempUnit : Float? = null,
    val txSetting : TxPower? = null,
    val gpio : Array<Gpio>? = null,
    val globalTrigSetting : GlobalTrigSetting? = null,
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
    val sensorTrigerSrc : Array<String>? = null,
    val trigCheckPeriod : Int? = null,
    val ui_format : String,
    val chCtrl: Int,
    val randomDlyType : Int
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

data class GlobalTrigSetting (
    val trig1Low : Int?,
    val trig2Low : Int?,
    val trig3Low : Int?,
    val trig4Low : Int?,
    val trig2High : Int?,
    val trig3High : Int?,
    val trig4High : Int?,
    val trig1Src : String?,
    val trig2Src : String?,
    val trig3Src : String?,
    val trig4Src : String?,
    val triggerEn : Array<Int>?
)

data class Gpio (
    val id : Int?,
    val digital : String?,
    val wakeup : String?,
    val advTrig : String?,
    val latch : Int?,
    val maskb : Int?
)

