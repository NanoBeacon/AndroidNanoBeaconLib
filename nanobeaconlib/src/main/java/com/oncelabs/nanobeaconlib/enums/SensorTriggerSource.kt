package com.oncelabs.nanobeaconlib.enums

enum class SensorTriggerSource(val abr : String, val fullName : String){
    T1LOW("t1Low","Low Trigger 1"),
    T2LOW("t2Low","Low Trigger 2"),
    T3LOW("t3Low","Low Trigger 3"),
    T4LOW("t4Low","Low Trigger 4"),
    T2HIGH("t2High","High Trigger 2"),
    T3HIGH("t3High","High Trigger 3"),
    T4HIGH("t4High","High Trigger 4");

    companion object {
        fun fromAbr(abr : String) : SensorTriggerSource? {
            return values().firstOrNull{ it.abr == abr }
        }
    }
}