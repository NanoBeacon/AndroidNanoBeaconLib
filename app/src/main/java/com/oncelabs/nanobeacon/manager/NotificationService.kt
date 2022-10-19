package com.oncelabs.nanobeacon.manager

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.oncelabs.nanobeacon.MainActivity
import com.oncelabs.nanobeacon.R
import java.io.File

class NotificationService : Service() {

    private var previousChannel = ""

    companion object {
        fun startService(context: Context, alertMsg : String, name : String, shouldSound : Boolean){//, fileName : String) {
            val startIntent = Intent(context, NotificationService::class.java)
            startIntent.putExtra("alertMsg", alertMsg)
            startIntent.putExtra("name", name)
            startIntent.putExtra("shouldSound", shouldSound)
            //startIntent.putExtra("fileName", fileName)
            //startIntent.putExtra("color", colorRes)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, NotificationService::class.java)
            context.stopService(stopIntent)

        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        //SessionManager.notificationJob?.cancel()
        stopForeground(true)
        stopSelf()
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("alertMsg") ?: " "
        val name = intent?.getStringExtra("name") ?: " "
        val shouldSound = intent?.getBooleanExtra("shouldSound", false)
        //val color = intent?.getIntExtra("color", R.color.no_alert) ?: R.color.no_alert
        val fileName = intent?.getStringExtra("fileName") ?: " "
        var sound: String = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/" + fileName//Here is FILE_NAME is the name of file that you want to play

        val file = File(sound)
        Log.d("Sound File Check", file.exists().toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (previousChannel != input) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
                previousChannel = input
                val channel = NotificationChannel(
                    input,
                    input,
                    NotificationManager.IMPORTANCE_HIGH,
                )
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

            }
        }


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, input)
            .setContentTitle(input)
            .setContentText(name)
            .setSmallIcon(R.drawable.nanobeacon_logo_white_s)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.nanobeacon_logo_white_s))
            .setContentIntent(pendingIntent)
            .setColor(Color.DKGRAY)
            .setColorized(true)
            .setSound(Uri.parse(sound))
            .setSilent(!((shouldSound) ?: false))


        val notification = builder.build()
        startForeground(1, notification)
        //stopSelf();
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return NotificationBinder()
    }

    inner class NotificationBinder : Binder() {
        val service: NotificationService
            get() = this@NotificationService
    }
}