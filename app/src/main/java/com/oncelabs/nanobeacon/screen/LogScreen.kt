package com.oncelabs.nanobeacon.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oncelabs.nanobeacon.components.*
import com.oncelabs.nanobeacon.model.FilterInputType
import com.oncelabs.nanobeacon.model.FilterOption
import com.oncelabs.nanobeacon.model.FilterType
import com.oncelabs.nanobeacon.ui.theme.InplayTheme
import com.oncelabs.nanobeacon.ui.theme.logFloatingButtonColor
import com.oncelabs.nanobeacon.ui.theme.logModalItemBackgroundColor
import com.oncelabs.nanobeacon.viewModel.LogViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LogScreen(
    logDataViewModel: LogViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val beaconDataLog by logDataViewModel.beaconDataEntries.observeAsState(initial = listOf())
    val filters by logDataViewModel.filters.observeAsState(initial = listOf())

    LogScreenContent(
        beaconDataLog = beaconDataLog,
        listState = listState,
        filters = filters,
        onFilterChange = logDataViewModel::setFilter
    )
}

@Composable
private fun LogScreenContent(
    beaconDataLog: List<BeaconDataEntry>,
    listState: LazyListState,
    filters: List<FilterOption>,
    onFilterChange: (FilterType, Any?, Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val modalIsOpen = remember { mutableStateOf(false)}
    var autoScrollEnabled by remember { mutableStateOf(true) }
    val searchText = remember { mutableStateOf(TextFieldValue("")) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    // listen for scroll events so we can disable auto-scroll
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // On scroll ended detection
                autoScrollEnabled = false
                return super.onPostFling(consumed, available)
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
                modifier = Modifier
                    .weight(1f),
                state = searchText,
                placeholder = "Placeholder Text"
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

        LazyColumn(
            modifier = Modifier
                .padding(bottom = 80.dp, top = 0.dp)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .nestedScroll(nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            items(
                if(searchText.value.text.isNotEmpty()) {
                    beaconDataLog.filter { it.searchableString.contains(searchText.value.text) }
                } else {
                    beaconDataLog
                }) {
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(0.1f))
                    Column(Modifier.weight(0.8f)) {
                        LogAdvertisementCard(data = it)
                    }
                    Spacer(Modifier.weight(0.1f))
                }
                Spacer(Modifier.height(20.dp))
            }

            // Scroll to last item whenever a new is added if enabled
            if (autoScrollEnabled && beaconDataLog.lastIndex != -1) {
                scope.launch {
                    listState.animateScrollToItem(beaconDataLog.lastIndex)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp, end = 20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            /**Scroll enable*/
            if (!autoScrollEnabled) {
                FloatingActionButton(
                    onClick = { autoScrollEnabled = true },
                    backgroundColor = logFloatingButtonColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        "Enable auto-scroll",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            /** Filter options*/
            FloatingActionButton(
                onClick = { modalIsOpen.value = true },
                backgroundColor = logFloatingButtonColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.FilterAlt, "filter Settings", modifier = Modifier.size(36.dp))
            }
        }

        ProjectConfigurationModal(
            isOpen = modalIsOpen.value,
            {
                modalIsOpen.value = false
            },
            listOf(
                "deneineffefe",
                "fenjfnjfewfdfsfwgwgwgarhqbebeERGahtghaq",
                "efw",
                "wfe",
                "wef",
                "wef",
                "Wef"
            )
        )
    }
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
            beaconDataLog = logs,
            listState = state,
            filters = listOf(),
            onFilterChange = { _, _, _ ->

            }
        )
    }
}