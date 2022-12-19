package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.QrCodeComponent
import com.oncelabs.nanobeacon.components.dialog.AddConfigModal
import com.oncelabs.nanobeacon.viewModel.LiveDataViewModel
import com.oncelabs.nanobeacon.viewModel.QrScanViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QrScanScreen(qrScanViewModel: QrScanViewModel = hiltViewModel()) {
    val showModal = qrScanViewModel.showModal.observeAsState()
    val configData = qrScanViewModel.stagedConfig.observeAsState()
    QrScanScreenContent(
        showModal = showModal.value,
        configData = configData.value,
        onQrCodeScanned = { qrScanViewModel.submitQrConfig(it) },
        dismissModal = { qrScanViewModel.declineConfig()},
        confirmConfig = { qrScanViewModel.confirmConfig()})
}


@Composable
fun QrScanScreenContent(
    showModal: Boolean?,
    configData: ConfigData?,
    onQrCodeScanned: (String) -> Unit,
    dismissModal : () -> Unit,
    confirmConfig : () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        AddConfigModal(
            configData = configData,
            shouldShow = showModal ?: false,
            title = "Add Config?",
            onDismiss = { dismissModal() },
            onConfirm = { confirmConfig() })

        Column(Modifier.fillMaxSize()) {
            QrCodeComponent(showModal = showModal ?: false, onCodeScanned = { onQrCodeScanned(it) })
        }
    }
}