package com.oncelabs.nanobeacon.device

import android.content.Context
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeacon.nanoBeaconLib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeacon
import com.oncelabs.nanobeacon.nanoBeaconLib.model.NanoBeaconData

class ICM20600(
    data: NanoBeaconData? = null,
    val context: Context? = null,
    delegate: NanoBeaconDelegate? = null
): NanoBeacon(
    beaconData = data,
    context = context,
    delegate = delegate
), CustomBeaconInterface {

    override fun isTypeMatchFor(
        beaconData: NanoBeaconData,
        context: Context,
        delegate: NanoBeaconDelegate
    ): NanoBeacon? {
        TODO("Not yet implemented")
    }
}
