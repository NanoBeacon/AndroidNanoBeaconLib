package com.oncelabs.nanobeaconlib.manager

import android.content.Context
import java.util.*
import kotlin.concurrent.timerTask

object NanoNotificationManager {
    lateinit var appContext: Context
    private var count : Int = 0
    private var timerRunning : Boolean = false

    fun submitNotification(set : Int?) {

        if (count < 1) {
            val num = set?.let { set.toString() } ?: ""
            NanoNotificationService.startService(
                NanoNotificationManager.appContext,
                "Inplay Alert",
                "Advertisement Triggered For Set $num",
                shouldSound = true,
                shouldVibrate = true
            ) //Trigger notification

            if (!timerRunning) {
                timerRunning = true
                Timer().schedule(timerTask {
                    count = 0
                    timerRunning = false
                }, 2000)
            }
            count += 1
        }
    }
}