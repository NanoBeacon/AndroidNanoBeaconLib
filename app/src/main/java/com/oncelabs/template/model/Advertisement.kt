package com.oncelabs.template.model

data class Advertisement(
    val advertisementId: String,
    val bleAddress: String,
    val rssi: String,
    val connectable: String,
    val manufacturerData: String,
    val powerLevel: String,
    val localName: String,
    val advInterval: String,
    val advMode: String,
    val sensorTriggerSource: String,
    val gpioTriggerSource: String,
    val dataEncryption: String
)