package com.oncelabs.nanobeacon.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.ConfigView
import com.oncelabs.nanobeacon.components.InplayTopBar
import com.oncelabs.nanobeacon.components.QrCodeComponent
import com.oncelabs.nanobeacon.components.dialog.AddConfigModal
import com.oncelabs.nanobeacon.components.dialog.ConfigConflictModal
import com.oncelabs.nanobeacon.enums.ConfigAdvConflicts
import com.oncelabs.nanobeacon.enums.ConflictItem
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
    val showConflictModal = qrScanViewModel.showConflicts.observeAsState()
    QrScanScreenContent(
        showModal = showModal.value,
        configData = currentConfig.value,
        onQrCodeScanned = { qrScanViewModel.submitQrConfig(it) },
        closeScanner = { qrScanViewModel.closeScanner() },
        buttonClicked = { qrScanViewModel.openScanner() },
        deleteConfig = { qrScanViewModel.deleteConfig() },
        showConflictModal = showConflictModal.value ?: false,
        dismissConflictModal = { qrScanViewModel.dismissConflictModal() },
        conflicts = qrScanViewModel.conflicts.observeAsState().value ?: listOf<ConflictItem>()
    )
}


@Composable
fun QrScanScreenContent(
    showModal: Boolean?,
    configData: ParsedConfigData?,
    onQrCodeScanned: (String) -> Unit,
    closeScanner : () -> Unit,
    buttonClicked: () -> Unit,
    deleteConfig : () -> Unit,
    showConflictModal : Boolean,
    dismissConflictModal : () -> Unit,
    conflicts: List<ConflictItem>
) {
    Box(Modifier.fillMaxSize()) {
        if (showModal == false) {
            Column(Modifier.fillMaxSize()) {
                InplayTopBar(title = "Configuration")
                Spacer(Modifier.height(7.dp))
                ConfigView(parsedConfigData = configData)
            }
            ConfigConflictModal(shouldShow = showConflictModal, conflicts = conflicts, onDismiss = { dismissConflictModal() })
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp, end = 20.dp),
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
                FloatingButton(
                    {
                        buttonClicked()
                    },
                    Icons.Default.QrCodeScanner
                )
            }
            if(configData != null) {
                Spacer(Modifier.height(25.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingButton(
                        {
                            deleteConfig()
                        },
                        Icons.Default.Delete
                    )
                }
            }

        }

        Box(Modifier.fillMaxSize()) {
            QrCodeComponent(showModal = showModal == false, onCodeScanned = { onQrCodeScanned(it) })
            if (showModal == true) {
                FloatingActionButton(
                    onClick = { closeScanner() },
                    backgroundColor = logModalItemBackgroundColor,
                    contentColor = Color.White,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Close Scanner",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingButton(
    onClick: () -> Unit,
    icon: ImageVector
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        backgroundColor = logFloatingButtonColor,
        contentColor = Color.White,
    ) {
        Icon(
            icon,
            "Top",
            modifier = Modifier.size(36.dp)
        )
    }
}