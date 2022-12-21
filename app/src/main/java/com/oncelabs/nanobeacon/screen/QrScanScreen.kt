package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.ConfigView
import com.oncelabs.nanobeacon.components.InplayTopBar
import com.oncelabs.nanobeacon.components.QrCodeComponent
import com.oncelabs.nanobeacon.components.dialog.AddConfigModal
import com.oncelabs.nanobeacon.ui.theme.logFloatingButtonColor
import com.oncelabs.nanobeacon.ui.theme.logModalItemBackgroundColor
import com.oncelabs.nanobeacon.viewModel.LiveDataViewModel
import com.oncelabs.nanobeacon.viewModel.QrScanViewModel
import com.oncelabs.nanobeaconlib.model.ParsedConfigData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QrScanScreen(qrScanViewModel: QrScanViewModel = hiltViewModel()) {
    val showModal = qrScanViewModel.showQrScanner.observeAsState()
    val currentConfig = qrScanViewModel.currentConfig.observeAsState()
    QrScanScreenContent(
        showModal = showModal.value,
        configData = currentConfig.value,
        onQrCodeScanned = { qrScanViewModel.submitQrConfig(it) },
        buttonClicked = { qrScanViewModel.openScanner() })
}


@Composable
fun QrScanScreenContent(
    showModal: Boolean?,
    configData: ParsedConfigData?,
    onQrCodeScanned: (String) -> Unit,
    buttonClicked : () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (showModal == false) {
            Column(Modifier.fillMaxSize()) {
                InplayTopBar(title = "Configuration")
                Spacer(Modifier.height(7.dp))
                ConfigView(parsedConfigData = configData)
            }
        }
        Column(
            Modifier.fillMaxSize().padding(bottom = 50.dp, end = 20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                    CameraButton {
                        buttonClicked()
                    }

            }
        }

        Column(Modifier.fillMaxSize()) {
            QrCodeComponent(showModal = showModal == false, onCodeScanned = { onQrCodeScanned(it) })
        }
    }
}

@Composable
fun CameraButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        backgroundColor = logFloatingButtonColor,
        contentColor = Color.White,
    ) {
        Icon(
            Icons.Default.QrCodeScanner,
            "Top",
            modifier = Modifier.size(36.dp)
        )
    }
}