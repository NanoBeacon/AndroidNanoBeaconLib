package com.oncelabs.nanobeaconlib.enums

enum class DynamicDataType(
    val fullName: String,
    val abrName: String,
) {

    VCC_ITEM("VCC", "VCC"),
    TEMP_ITEM("Internal Temperature", "TEMP"),
    PULSE_ITEM("1-Wire Count", "1-WIRE"),
    GPIO_ITEM("GPIO Status", "GPIO"),
    AON_GPIO_ITEM("AON GPIO Status", "AON-GPIO"),
    EDGE_CNT_ITEM("GPIO Edge Count", "EDGE"),
    ADC_CH0_ITEM("ADC CH0", "ADC CH0"),
    ADC_CH1_ITEM("ADC CH1", "ADC CH1"),
    ADC_CH2_ITEM("ADC CH2", "ADC CH2"),
    ADC_CH3_ITEM("ADC CH3", "ADC CH3"),
    REG1_ITEM("I2C Slave #1 Read Data", "I2C1R"),
    REG2_ITEM("I2C Slave #2 Read Data", "I2C2R"),
    REG3_ITEM("I2C Slave #3 Read Data", "I2C3R"),
    QDEC_ITEM("Quadrature Decode Value", "QDEC"),
    TS0_ITEM("Time Stamp 0", "TS0"),
    TS1_ITEM("Time Stamp 1", "TS1"),
    ADVCNT_ITEM("ADV Count", "ADVCNT"),
    REG_ITEM("Register Read Data", "REG"),
    RANDOM_ITEM("Random Number", "RANDOM"),
    STATIC_RANDOM_ITEM("Static Random Number", "RANDOM2"),
    ENCRYPT_ITEM("Encrypt Raw", "EncRaw"),
    SALT_ITEM("EAX Salt", "SALT"),
    TAG_ITEM("Auth Tag", "TAG"),
    CUSTOM_PRODUCT_ID_ITEM("Customer Product ID", "CustID"),
    BLUETOOTH_DEVICE_ADDRESS_ITEM("Bluetooth Device Address", "BDADDR"),
    UTF8_ITEM("Characters UTF-8", "utf8");

    companion object {
        fun fromAbr(abr: String): DynamicDataType? {
            return values().firstOrNull { it.abrName == abr }
        }


    }
}