package com.oncelabs.nanobeacon.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.codable.ConfigData
import com.oncelabs.nanobeacon.components.*
import com.oncelabs.nanobeacon.model.BeaconType
import com.oncelabs.nanobeacon.model.FilterInputType
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeacon.ui.theme.*
import com.oncelabs.nanobeacon.viewModel.ScannerViewModel
import com.oncelabs.nanobeaconlib.interfaces.NanoBeaconInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalMaterialApi
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val filters by viewModel.filters.observeAsState(initial = listOf())
    val scanEnabled by viewModel.scanningEnabled.observeAsState(initial = false)
    val discoveredBeacons by viewModel.filteredDiscoveredBeacons.observeAsState(initial = listOf())
    val savedConfigs by viewModel.savedConfigs.observeAsState()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = scope.launch {
        refreshing = true
        viewModel.refresh()
        delay(1500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(modifier = Modifier.pullRefresh(state)) {
        ScannerContent(
            scanEnabled,
            discoveredBeacons,
            listState = listState,
            filters = filters,
            savedConfigs = savedConfigs ?: listOf(),
            onFilterChange = viewModel::onFilterChanged,
            onScanButtonClick = if (scanEnabled) viewModel::stopScanning else viewModel::startScanning,
            onRefreshButtonClick = viewModel::refresh,
            openFilePickerManager = { viewModel.openFilePickerManager() }
        )
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}

@ExperimentalMaterialApi
@Composable
private fun ScannerContent(
    scanningEnabled: Boolean,
    discoveredBeacons: List<NanoBeaconInterface>,
    listState: LazyListState,
    filters: List<FilterOption>,
    savedConfigs: List<ConfigData>,
    onFilterChange: (FilterType, Any?, Boolean) -> Unit,
    onScanButtonClick: () -> Unit,
    onRefreshButtonClick: () -> Unit,
    openFilePickerManager: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalIsOpen = remember { mutableStateOf(true) }
    var autoScrollEnabled by remember { mutableStateOf(true) }
    val filterByNameText = rememberSaveable { mutableStateOf("") }
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

    fun scrollToTopAndPause() {
        scope.launch {
            if (discoveredBeacons.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }
        autoScrollEnabled = false
    }

    Column {
        /**Top bar*/
        InplayTopBar(title = "Scanner")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(logModalItemBackgroundColor)
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /**Name filter*/
//            Text("name"
//                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
//            FilterTextField(
//                modifier = Modifier.weight(1f),
//                state = filterByNameText,
//                placeholder = "Filter by name",
//            )
            //SearchView(
            //    modifier = Modifier.weight(1f),
            //    state = filterByNameText,
            //    placeholder = "Filter by name",
            //    leadingIcon = Icons.Default.Search
            //)

            Spacer(Modifier.weight(1f))
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
                items(discoveredBeacons) {
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
            if (!autoScrollEnabled) {
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

//        ProjectConfigurationModal(
//            isOpen = modalIsOpen.value,
//            {
//                modalIsOpen.value = false
//            },
//            savedConfigs,
//            openFilePickerManager
//        )
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

        /**Scroll to top*/
        FloatingActionButton(
            onClick = {
                // Scroll to top
                scrollToTopAndPause()
            },
            backgroundColor = logFloatingButtonColor,
            contentColor = Color.White,
        ) {
            Icon(
                Icons.Filled.VerticalAlignTop,
                "Top",
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(25.dp))


        /**Start/stop scanning*/
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
    when (filter.filterType.getInputType()) {
        FilterInputType.BINARY -> BinaryFilterCard(
            filter = filter,
            onChange = {
                onFilterChange(filter.filterType, it, it)
            }
        )
        FilterInputType.SLIDER -> SliderFilterCard(
            filter = filter,
            onChange = {
                onFilterChange(filter.filterType, it, true)
            }
        )
        FilterInputType.SEARCH -> SearchFilterCard(
            filter = filter,
            onChange = {
                onFilterChange(filter.filterType, it, true)
            }
        )
        FilterInputType.OPTIONS -> GroupedOptionsFilterCard(
            filter = filter,
            onChange = {
                onFilterChange(filter.filterType, it, true)
            }
        )
    }
}

@Composable
private fun BinaryFilterCard(
    filter: FilterOption,
    onChange: (Boolean) -> Unit
) {
    val checkedState =
        filter.value as? Boolean ?: filter.filterType.getDefaultValue() as? Boolean ?: false

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            filter.filterType.getName(),
            modifier = Modifier
                .weight(1f)
        )
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                onChange(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = iconSelected,
                uncheckedColor = Color.Gray,
                checkmarkColor = MaterialTheme.colors.primary
            )
        )
    }
}

@Composable
private fun SearchFilterCard(
    filter: FilterOption,
    onChange: (String) -> Unit
) {
    val value = rememberSaveable { mutableStateOf(filter.value as? String ?: "") }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(filter.filterType.getName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            modifier = Modifier.weight(1f)
        )
        FilterTextField(
            modifier = Modifier.weight(3f),
            state = value,
            placeholder = "Filter by ${filter.filterType.getName()}",
            onValueChange = onChange
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
        Spacer(modifier = Modifier.weight(.25f))
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
    }
}

@Composable
private fun GroupedOptionsFilterCard(
    filter: FilterOption,
    onChange: (MutableMap<String, Boolean>) -> Unit
) {
    val optionMap = (filter.value as? MutableMap<String, Boolean>) ?: mapOf()
    val localMap = optionMap.toMutableMap()
    val enabledOptions = optionMap.filter { it.value }.keys
    var expanded by remember { mutableStateOf(false) }

    fun editMap(type: String, value: Boolean) {
        localMap[type] = value
        onChange(localMap)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${filter.filterType.getName()}: ${enabledOptions.joinToString(", ")}",
            modifier = Modifier
                .weight(1f)
        )
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Image(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
            }

            // drop down menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                // adding items
                optionMap.keys.forEachIndexed { _, itemValue ->
                    DropdownMenuItem(
                        onClick = {}
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = itemValue,
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = optionMap[itemValue] ?: false,
                                onCheckedChange = {
                                    editMap(type = itemValue, value = it)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = iconSelected,
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = MaterialTheme.colors.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview
fun PreviewLogScreen() {
    InplayTheme {
        val logsCopy = mutableListOf<BeaconDataEntry>()
        val logs = remember { mutableStateListOf<BeaconDataEntry>() }
        val state = rememberLazyListState()

        // Randomly add beacons to list to simulate scanning
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                logsCopy.add(BeaconDataEntry.getRandomBeaconDataEntry())
                logs.clear()
                logs.addAll(logsCopy)
                Log.d("foo", logs.size.toString())
            }
        }

        ScannerContent(
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