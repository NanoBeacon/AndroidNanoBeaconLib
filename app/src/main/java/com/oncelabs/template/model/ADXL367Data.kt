package com.oncelabs.template.model

data class ADXL367Data(
    var xAccel: Float,
    var yAccel: Float,
    var zAccel: Float,
    var temp: Float,
    var rssi: Int
)
