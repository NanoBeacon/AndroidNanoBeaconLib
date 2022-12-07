package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oncelabs.nanobeacon.components.QrCodeComponent

@Composable
fun QrScanScreen() {
    QrScanScreenContent(onQrCodeScanned = {})
}


@Composable
fun QrScanScreenContent(onQrCodeScanned : (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        QrCodeComponent(showModal = false, onCodeScanned = {})
    }
}