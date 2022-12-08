package com.oncelabs.nanobeacon.analyzer

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.ZipException


class QRCodeAnalyzer(
    private var onQrCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val img = image.image

        if (img != null && image.width != 0) {
            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        val valueType = barcode.valueType
                        // See API reference for complete list of supported types
                        Log.d("QR Code", "$rawValue $valueType")
                        when (valueType) {
                            Barcode.TYPE_TEXT -> {
                                Log.d("QRCODE SCANNED", rawValue ?: "")
                                //SessionManager._showQrMsg.value = true
                                onQrCodeScanned(rawValue ?: "")
                            }
                            //Barcode.TYPE
                        }
                    }
                }
                .addOnFailureListener {
                }
                .addOnCompleteListener {
                    image.close()
                }


        }
        else {
            image.close()
        }
    }
}
fun ByteArray.gzipDecompress(): ByteArray {
    val bais = ByteArrayInputStream(this)
    try {
        GZIPInputStream(bais).use { return it.readBytes() }
    } catch (e : ZipException) {
        Log.d("ERror", e.toString())
        return byteArrayOf()
    }
}