package com.oncelabs.nanobeacon.enums

import android.util.Log
import com.oncelabs.nanobeacon.codable.AdvSetData
import com.oncelabs.nanobeaconlib.model.ParsedAdvertisementData

enum class ConfigAdvConflicts(val clearIssue: (p1: AdvSetData, p2: AdvSetData) -> Boolean) {
    //Comparison
    BDADDR_CONFLICT(clearIssue = { p1, p2 ->
        if (p1.bdAddr != null && p2.bdAddr != null) {
            (p1.bdAddr != p2.bdAddr)
        } else {
            true
        }
    });

    companion object {
        fun checkAdvs(parsedAdvs : Array<AdvSetData>) : List<ConflictItem> {
            var conflicts = mutableListOf<ConflictItem>()
            for (index in parsedAdvs.indices) {
                val currentAdv = parsedAdvs[index]
                var checkIndex = index + 1
                while (checkIndex < parsedAdvs.size) {
                    for (conflict in values()) {

                        if (!conflict.clearIssue(currentAdv, parsedAdvs[checkIndex])) {
                            conflicts.add(ConflictItem(currentAdv, parsedAdvs[checkIndex], conflict))
                        }
                    }
                    checkIndex++
                }
            }
            return conflicts
        }
    }
}

data class ConflictItem (
    var firstAdv : AdvSetData,
    var secondAdv : AdvSetData,
    var configAdvConflict : ConfigAdvConflicts
)