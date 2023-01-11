package com.oncelabs.nanobeacon.device

import android.content.Context
import com.oncelabs.nanobeaconlib.interfaces.CustomBeaconInterface
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconDelegate
import com.oncelabs.nanobeaconlib.model.NanoBeacon
import com.oncelabs.nanobeaconlib.model.NanoBeaconData
import java.lang.ref.WeakReference

class ICM20600(
    data: NanoBeaconData? = null,
    val context: Context? = null,
    delegate: NanoBeaconDelegate? = null
): NanoBeacon(
    beaconData = data,
    context = context,
    delegate = WeakReference(delegate)
), CustomBeaconInterface {

    override fun isTypeMatchFor(
        beaconData: NanoBeaconData,
        context: Context,
        delegate: NanoBeaconDelegate
    ): NanoBeacon? {
        TODO("Not yet implemented")
    }
}
