package com.oncelabs.nanobeacon.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.*
import com.oncelabs.nanobeacon.model.FilterInputType
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeacon.ui.theme.InplayTheme
import com.oncelabs.nanobeacon.ui.theme.autoScrollTogleFont
import com.oncelabs.nanobeacon.ui.theme.logFloatingButtonColor
import com.oncelabs.nanobeacon.ui.theme.logModalItemBackgroundColor
import com.oncelabs.nanobeacon.viewModel.LogViewModel
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LogScreen(
    logDataViewModel: LogViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val filters by logDataViewModel.filters.observeAsState(initial = listOf())
    val scanEnabled by logDataViewModel.scanningEnabled.observeAsState(initial = true)
    val discoveredBeacons by logDataViewModel.filteredDiscoveredBeacons.observeAsState(initial = listOf())
    val savedConfigs by logDataViewModel.savedConfigs.observeAsState()
    LogScreenContent(
        scanEnabled,
        discoveredBeacons,
        listState = listState,
        filters = filters,
        savedConfigs = savedConfigs ?: listOf(),
        onFilterChange = logDataViewModel::setFilter,
        onScanButtonClick = if (scanEnabled) logDataViewModel::stopScanning else logDataViewModel::startScanning,
        onRefreshButtonClick = logDataViewModel::refresh,
        openFilePickerManager = {logDataViewModel.openFilePickerManager() }
    )
}

@Composable
private fun LogScreenContent(
    scanningEnabled: Boolean,
    discoveredBeacons: List<NanoBeaconInterface>,
    listState: LazyListState,
    filters: List<FilterOption>,
    savedConfigs : List<ConfigData>,
    onFilterChange: (FilterType, Any?, Boolean) -> Unit,
    onScanButtonClick: () -> Unit,
    onRefreshButtonClick: () -> Unit,
    openFilePickerManager: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalIsOpen = remember { mutableStateOf(true)}
    var autoScrollEnabled by remember { mutableStateOf(true) }
    val searchText = rememberSaveable { mutableStateOf("") }
    var filterMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var actionButtonExpanded by rememberSaveable { mutableStateOf(false) }

    // listen for scroll events so we can disable auto-scroll
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                autoScrollEnabled = false
                return super.onPreScroll(available, source)
            }
        }
    }
    Column {
        /**Top bar*/
        InplayTopBar(title = "Log")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /**Search results*/
            SearchView(
                modifier = Modifier.weight(1f),
                state = searchText,
                placeholder = "BT Addr, Manufacturer Data..."
            )

            /**Filter results drop down*/
            FilterButton {
                filterMenuExpanded = !filterMenuExpanded
            }
        }

        /**Filter view*/
        ExpandableCard(expanded = filterMenuExpanded) {
            FilterView(
                filters = filters,
                onFilterChange = onFilterChange
            )
        }

        Column {
            LazyColumn(
                modifier = Modifier
                    .weight(0.925f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .nestedScroll(nestedScrollConnection)
                    .padding(bottom = 0.dp, top = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState
            ) {
                items(
                    if(searchText.value.isNotEmpty()) {
                        discoveredBeacons.filter {
                            it.beaconDataFlow.value?.searchableString?.contains(searchText.value, ignoreCase = true) == true
                        }
                    } else {
                        discoveredBeacons
                    }) {
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(0.025f))
                        Column(Modifier.weight(0.95f)) {
                            LogAdvertisementCard(beacon = it)
                        }
                        Spacer(Modifier.weight(0.025f))
                    }
                    Spacer(Modifier.height(10.dp))

                    // Scroll to last item whenever a new is added if enabled
                    if (autoScrollEnabled && discoveredBeacons.lastIndex != -1) {
                        LaunchedEffect(Unit) {
                            scope.launch {
                                listState.animateScrollToItem(discoveredBeacons.lastIndex)
                            }
                        }
                    }
                }
            }
            if (!autoScrollEnabled){
                Box(
                    modifier = Modifier
                        .background(logFloatingButtonColor.copy(0.87f))
                        .weight(0.075f)
                        .fillMaxWidth()
                        .clickable {
                            autoScrollEnabled = !autoScrollEnabled
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enable Scrolling",
                        style = autoScrollTogleFont
                    )
                }
            }
        }

        ProjectConfigurationModal(
            isOpen = modalIsOpen.value,
            {
                modalIsOpen.value = false
            },
            savedConfigs,
            openFilePickerManager
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp, end = 20.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

            if (!scanningEnabled) {
                FloatingActionButton(
                    onClick = {
                        onRefreshButtonClick()
                    },
                    backgroundColor = logFloatingButtonColor,
                    contentColor = Color.White,
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        "Refresh Button",
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(25.dp))
            }
            /**Scroll enable*/
            FloatingActionButton(
                onClick = {
                    onScanButtonClick()
                },
                backgroundColor = logFloatingButtonColor,
                contentColor = Color.White,
            ) {
                Icon(
                    if (scanningEnabled) Icons.Default.Stop else Icons.Default.PlayArrow,
                    "Start Stop",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

//        Spacer(Modifier.height(8.dp))
//
//        /** Filter options*/
//        FloatingActionButton(
//            onClick = { modalIsOpen.value = true },
//            backgroundColor = logFloatingButtonColor,
//            contentColor = Color.White
//        ) {
//            Icon(Icons.Default.FilterAlt, "filter Settings", modifier = Modifier.size(36.dp))
//        }

}

@Composable
fun FilterButton(
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .background(logModalItemBackgroundColor)
            .fillMaxHeight(),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = "Tune",
            tint = Color.White
        )
    }
}

@Composable
private fun FilterView(
    filters: List<FilterOption>,
    onFilterChange: (FilterType, Any?, Boolean) -> Unit
) {
    Column(Modifier.padding(8.dp)) {
        filters.forEach {
            FilterCard(filter = it, onFilterChange = onFilterChange)
        }
    }
}

@Composable
private fun FilterCard(
    filter: FilterOption,
    onFilterChange: (FilterType, Any?, Boolean) -> Unit
) {
    when(filter.filterType.getInputType()) {
        FilterInputType.BINARY -> {/**Probably a toggle button*/}
        FilterInputType.SLIDER -> SliderFilterCard(
            filter = filter,
            onChange = {
                onFilterChange(filter.filterType, it, false)
            }
        )
    }
}

/**
 * Filter cards to be used with Slider filters
 */
@Composable
private fun SliderFilterCard(
    filter: FilterOption,
    onChange: (Float) -> Unit
) {
    check(filter.filterType.getInputType() == FilterInputType.SLIDER)
    val lower: Float = filter.filterType.getRange()?.first?.toFloat() ?: 0f
    val upper: Float = filter.filterType.getRange()?.second?.toFloat() ?: 100f
    val range = lower..upper

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            filter.filterType.getName(),
            modifier = Modifier
                .weight(1f)
        )
        Slider(
            value = filter.value as? Float ?: 0f,
            onValueChange = {
                onChange(it)
            },
            valueRange = (
                range
            ),
            modifier = Modifier
                .weight(2f)
        )
        Spacer(modifier = Modifier.weight(.25f))
        Text(
            (filter.value as? Float)?.toInt().toString(),
            modifier = Modifier
                .weight(.5f)
        )
        Spacer(modifier = Modifier.weight(.25f))
    }
}

@Composable
@Preview
fun PreviewLogScreen() {
    InplayTheme {
        val logsCopy = mutableListOf<BeaconDataEntry>()
        val logs = remember { mutableStateListOf<BeaconDataEntry>() }
        val state = rememberLazyListState()

        // Randomly add beacons to list to simulate scanning
        LaunchedEffect(Unit) {
            while(true) {
                delay(1000)
                logsCopy.add(BeaconDataEntry.getRandomBeaconDataEntry())
                logs.clear()
                logs.addAll(logsCopy)
                Log.d("foo", logs.size.toString())
            }
        }

        LogScreenContent(
            true,
            discoveredBeacons = listOf(),
            listState = state,
            filters = listOf(),
            savedConfigs = listOf(),
            onFilterChange = { _, _, _ ->

            },
            onScanButtonClick = {

            },
            onRefreshButtonClick = {

            },
            openFilePickerManager = {

            }
        )
    }
}