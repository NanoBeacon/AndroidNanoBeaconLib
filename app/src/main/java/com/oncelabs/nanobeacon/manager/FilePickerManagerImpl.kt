package com.oncelabs.nanobeacon.manager

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.beust.klaxon.Klaxon
import com.oncelabs.nanobeacon.codable.ConfigData
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePickerManagerImpl @Inject constructor() : FilePickerManager {

    val PICK_CFG_FILE = 1
    private var activity : Activity? = null
    override fun onResultFromActivity(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.let {
            val cR: ContentResolver = it.contentResolver
            val mime = MimeTypeMap.getSingleton()
            Log.d("Filer", data?.data.toString())

            data?.data?.let {
                val x = cR.openInputStream(it)

                val reader = BufferedReader(InputStreamReader(x))
                var holder = ""
                var t = reader.readLine()
                while (t != null) {
                    holder += t
                    t = reader.readLine()
                }
                Log.d("Filer", holder)
                val json = Klaxon().parse<ConfigData>(holder)
                Log.d("JSON", json.toString())
            }
        }
    }

    override fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/octet-stream"))
            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
        }

        activity?.startActivityForResult(intent, PICK_CFG_FILE)
    }

    override fun createActivity(act : Activity) {
        activity?.let {} ?: run { activity = act }
    }

}