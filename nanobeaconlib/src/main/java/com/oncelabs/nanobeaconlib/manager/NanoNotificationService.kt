package com.oncelabs.nanobeaconlib.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.oncelabs.nanobeaconlib.R
import java.io.File
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

interface ServiceUnbinder {
    fun unbind()
}

class NanoNotificationService : LifecycleService() {

    companion object {

        private val TAG = NanoNotificationService::class.java.simpleName

        fun startService(
            context: Context,
            alertMsg: String,
            name: String,
            shouldSound: Boolean,
            shouldVibrate: Boolean,
        ) {
            val startIntent = Intent(context, NanoNotificationService::class.java)
            startIntent.putExtra("alertMsg", alertMsg)
            startIntent.putExtra("name", name)
            startIntent.putExtra("shouldSound", shouldSound)
            startIntent.putExtra("shouldVibrate", shouldVibrate)
            ContextCompat.startForegroundService(context, startIntent)
        }

    }

    private var serviceUnbinder: WeakReference<ServiceUnbinder?> = WeakReference(null)
    private val notificationBinder: IBinder = NotificationBinder()

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroying self")
        //removeAllNotifications()
    }

    fun setUnbinder(unbinder: ServiceUnbinder){
        serviceUnbinder = WeakReference(unbinder)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "On Task Removed")
        super.onTaskRemoved(rootIntent)
        serviceUnbinder.get()?.unbind()
        stopService()
        stopSelf()
        exitProcess(0)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return notificationBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //do heavy work on a background thread
        val input = ((intent?.getStringExtra("alertMsg") ?: " "))
        val name = intent?.getStringExtra("name") ?: " "
        val shouldSound = intent?.getBooleanExtra("shouldSound", false)
        val shouldVibrate = intent?.getBooleanExtra("shouldVibrate", false)


        val manager = (getSystemService(NotificationManager::class.java))
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            input,
            input,
            importance,
        ).apply {
            vibrationPattern = longArrayOf(100L, 100L, 100L)
            enableVibration(shouldVibrate ?: false)
        }
        manager?.createNotificationChannel(channel)

        val notificationIntent = Intent(this, NanoNotificationService::class.java)

        val pendingIntent =
            PendingIntent
                .getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

        val builder =
            NotificationCompat
                .Builder(this, input)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Advertisement Triggered")
                .setContentText(name)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(1000L, 1000L, 1000L))

        val notification = builder.build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    fun stopService() {
        stopForeground(true)
    }

    private fun removeAllNotifications() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    inner class NotificationBinder : Binder() {
        val service: NanoNotificationService
            get() = this@NanoNotificationService
    }
}