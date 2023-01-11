package com.oncelabs.nanobeacon.enums

import android.util.Log
import com.oncelabs.nanobeacon.codable.AdvSetData
import com.oncelabs.nanobeaconlib.model.ParsedAdvertisementData

enum class ConfigAdvConflicts(
    val clearIssue: (p1: ParsedAdvertisementData, p2: ParsedAdvertisementData) -> Boolean,
    val getMsg: (id1: String, id2: String) -> String
) {
    //Comparison
    BDADDR_CONFLICT(clearIssue = { p1, p2 ->
        if (p1.bdAddr != null && p2.bdAddr != null) {
            var size1 = 0
            var size2 = 0
            p1.parsedPayloadItems?.manufacturerData?.let { stagedData ->
                for (payload in stagedData) {
                    size1 += payload.len
                }
            }
            p2.parsedPayloadItems?.manufacturerData?.let { stagedData ->
                for (payload in stagedData) {
                    size2 += payload.len
                }
            }
            (p1.bdAddr != p2.bdAddr || size1 != size2)
        } else {
            true
        }
    },
        getMsg = { id1, id2 ->
            "Advertisement sets ${id1} and ${id2} have the same bluetooth address and data set length"
        }
    ),
    ;

    companion object {
        fun checkAdvs(parsedAdvs: Array<ParsedAdvertisementData>): List<ConflictItem> {
            var conflicts = mutableListOf<ConflictItem>()
            for (index in parsedAdvs.indices) {
                val currentAdv = parsedAdvs[index]
                var checkIndex = index + 1
                while (checkIndex < parsedAdvs.size) {
                    for (conflict in values()) {

                        if (!conflict.clearIssue(currentAdv, parsedAdvs[checkIndex])) {
                            conflicts.add(
                                ConflictItem(
                                    currentAdv,
                                    parsedAdvs[checkIndex],
                                    conflict
                                )
                            )
                        }
                    }
                    checkIndex++
                }
            }
            return conflicts
        }
    }
}

data class ConflictItem(
    var firstAdv: ParsedAdvertisementData,
    var secondAdv: ParsedAdvertisementData,
    var configAdvConflict: ConfigAdvConflicts,
    val getMsg : () -> String = { configAdvConflict.getMsg(firstAdv.id.toString(), secondAdv.id.toString())}
)