package com.oncelabs.nanobeacon.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.oncelabs.nanobeacon.analyzer.QRCodeAnalyzer


@Composable
fun QrCodeComponent(showModal: Boolean, onCodeScanned : (String) -> Unit) {
    var code by remember {
        mutableStateOf("")
    }
    var hasReadCode by remember {
        mutableStateOf(false)
    }
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFeature = ProcessCameraProvider.getInstance(context)

    LaunchedEffect(showModal) {
        if (!showModal) {
            cameraProviderFeature.get().unbindAll()
        }
    }




    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (hasCamPermission) {
            if (hasReadCode || showModal) {
                cameraProviderFeature.get().unbindAll()
            } else {
                val previewView = remember { PreviewView(context) }
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(
                        Size(
                            previewView.width,
                            previewView.height
                        )
                    )
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRCodeAnalyzer { result ->
                        onCodeScanned(result)
                    }
                )

                try {
                    cameraProviderFeature.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                LaunchedEffect(lensFacing) {
                    cameraProviderFeature.get().unbindAll()
                    cameraProviderFeature.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
