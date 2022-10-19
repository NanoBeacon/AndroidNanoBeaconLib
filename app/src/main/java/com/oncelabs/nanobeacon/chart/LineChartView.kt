package com.oncelabs.nanobeacon.chart

import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.oncelabs.nanobeacon.R
import com.oncelabs.nanobeacon.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LineChartView(
    modifier: Modifier = Modifier,
    chartData: List<Pair<Float, Float>>,
) {
    lateinit var lineChart: LineChart

    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = {
            View.inflate(it, R.layout.line_chart, null)
        },
        update = { view ->
            lineChart = view.findViewById(R.id.lineChart)
            initializeGraph(
                lineChart = lineChart,
                labelColor = Color.White,
                backgroundColor = cardBackground
            )
            updateGraphWithData(
                lineChart,
                chartData,
                chartColor
            )
        }
    )
}


private fun initializeGraph(
    lineChart: LineChart,
    labelColor: Color,
    backgroundColor: Color,
) {
    lineChart.setTouchEnabled(false)
    lineChart.isScaleYEnabled = true
    lineChart.axisRight.isEnabled = false
    lineChart.description.isEnabled = false


    lineChart.axisLeft.axisLineColor = axisColor.hashCode()

    lineChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    lineChart.axisLeft.setLabelCount(5, true)
    lineChart.axisLeft.setDrawGridLines(true)
    lineChart.axisLeft.gridLineWidth = 0.6f

    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    lineChart.xAxis.axisLineColor = axisColor.hashCode()
    lineChart.xAxis.setDrawGridLines(true)
    lineChart.xAxis.setDrawLabels(false)
    lineChart.xAxis.gridLineWidth = 0.6f
    lineChart.xAxis.setLabelCount(9, true)

    lineChart.axisLeft.textColor = labelColor.hashCode()
    lineChart.legend.isEnabled = false
    lineChart.description.textColor = labelColor.hashCode()
    lineChart.setBackgroundColor(backgroundColor.hashCode())
}

private fun updateGraphWithData(
    lineChart: LineChart,
    chartData: List<Pair<Float, Float>>,
    lineColor: Color,
) {
    val data = mutableStateListOf<Entry>()

    // Data entries
    for (i in chartData) {
        data.add(Entry(i.first,i.second))
    }
    //Collections.sort(data, EntryXComparator())

    val lineDataSet = LineDataSet(data, "")

    lineDataSet.colors = listOf(lineColor.hashCode())
    lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    lineDataSet.lineWidth = 2.0f
    lineDataSet.setDrawCircles(false)
    lineDataSet.setDrawValues(false)
    lineDataSet.setDrawFilled(true)
    lineDataSet.fillColor = chartColor.hashCode()

    val lineData = LineData(lineDataSet)
    lineChart.data = lineData
    lineChart.invalidate()
}


@Preview
@Composable
private fun PreviewLineChart() {
    val chartData = remember { mutableStateListOf<Pair<Float, Float>>() }

    LaunchedEffect(Unit) {
        for(i in 1..100) {
            delay(500)
            chartData.clear()
            val tempList = listOf(
                Pair(i.toFloat()+2f, -1f),
                Pair(i.toFloat()+4f, 1f),
                Pair(i.toFloat()+6f, 2f),
                Pair(i.toFloat()+8f, 3f),
                Pair(i.toFloat()+10f, 4f),
                Pair(i.toFloat()+12f, 5f),
            )
            chartData.addAll(tempList)
            chartData.forEach { Log.d("Preview", "Chart value -> $it") }
        }
    }
    InplayTheme {
        LineChartView(
            chartData = chartData,
        )
    }
}