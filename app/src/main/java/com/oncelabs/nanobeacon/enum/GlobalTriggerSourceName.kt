package com.oncelabs.nanobeacon.enum

enum class GlobalTriggerSourceName(val abrv : String, val fullName : String) {
    VCC("vcc", "VCC"),
    TEMP("temp", "Internal Temperature"),
    ADC_CH0("ch0","ADC CH0"),
    ADC_CH2("ch2","ADC CH2"),
    ADC_CH3("ch3","ADC CH3"),
    WIRE_SENSOR("pulse", "1-Wire Sensor"),
    I2C0("i2c", "I2C Slave 1"),
    I2C1("i2c1", "I2C Slave 2");

    companion object {
        fun fullNameFromAbrv(ab: String?): String? {
            ab?.let {
                val holder = values().firstOrNull { it.abrv == ab }
                if (holder != null) {
                    return holder.fullName
                }
            }
            return null
        }
    }


}