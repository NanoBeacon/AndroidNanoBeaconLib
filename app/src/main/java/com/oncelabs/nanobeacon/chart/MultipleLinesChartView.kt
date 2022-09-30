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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.oncelabs.nanobeacon.R
import com.oncelabs.nanobeacon.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MultipleLinesChartView(
    modifier: Modifier = Modifier,
    chartData: List<List<Pair<Float, Float>>>,
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
    lineChart.axisLeft.setLabelCount(3, true)
    lineChart.axisLeft.setDrawGridLines(true)
    lineChart.axisLeft.gridLineWidth = 0.6f

    lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
    lineChart.xAxis.axisLineColor = axisColor.hashCode()
    lineChart.xAxis.setDrawGridLines(true)
    lineChart.xAxis.setDrawLabels(false)
    lineChart.xAxis.gridLineWidth = 0.6f
    lineChart.xAxis.setLabelCount(9, true)

    lineChart.axisLeft.textColor = labelColor.hashCode()
    lineChart.legend.isEnabled = true

    val zL = LegendEntry("Z: +1 [g]", Legend.LegendForm.CIRCLE,10f,2f,null, zLineColor.hashCode())
    val xL = LegendEntry("X: -1 [g]", Legend.LegendForm.CIRCLE,10f,2f,null, xLineColor.hashCode())
    val yL = LegendEntry("Y: +1 [g]", Legend.LegendForm.CIRCLE,10f,2f,null, yLineColor.hashCode())

    lineChart.legend.setCustom(listOf(zL, xL, yL))
    lineChart.legend.textColor = Color.White.hashCode()
    lineChart.description.textColor = labelColor.hashCode()
    lineChart.setBackgroundColor(backgroundColor.hashCode())
}

private fun updateGraphWithData(
    lineChart: LineChart,
    chartData: List<List<Pair<Float, Float>>>,
    lineColor: Color,
) {
    val unProcessedData1 = mutableStateListOf<Entry>()
    val unProcessedData2 = mutableStateListOf<Entry>()
    val unProcessedData3 = mutableStateListOf<Entry>()

    for (i in chartData[0]) {
        unProcessedData1.add(Entry(i.first,i.second))
    }

    for (i in chartData[1]) {
        unProcessedData2.add(Entry(i.first,i.second))
    }

    for (i in chartData[2]) {
        unProcessedData3.add(Entry(i.first,i.second))
    }

    val set1 = LineDataSet(unProcessedData1, "Z: +1 [g]")
    set1.colors = listOf(zLineColor.hashCode())
    set1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set1.lineWidth = 2.0f
    set1.setDrawCircles(false)
    set1.setDrawValues(false)
    set1.label = "Z: +1 [g]"


    val set2 = LineDataSet(unProcessedData2, "X: -1 [g]")
    set2.colors = listOf(xLineColor.hashCode())
    set2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set2.lineWidth = 2.0f
    set2.setDrawCircles(false)
    set2.setDrawValues(false)


    val set3 = LineDataSet(unProcessedData3, "Y: -1 [g]")
    set3.colors = listOf(yLineColor.hashCode())
    set3.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set3.lineWidth = 2.0f
    set3.setDrawCircles(false)
    set3.setDrawValues(false)



    val lineDataSets : MutableList<ILineDataSet> = mutableListOf()

    lineDataSets.add(set1)
    lineDataSets.add(set2)
    lineDataSets.add(set3)



    val lineData = LineData(lineDataSets)
    lineChart.data = lineData
    lineChart.invalidate()
}


@Preview
@Composable
private fun PreviewMultipleLinesChart() {
    val chartData = remember { mutableStateListOf<Pair<Float, Float>>() }
    val chartData2 = remember { mutableStateListOf<Pair<Float, Float>>() }
    val chartData3 = remember { mutableStateListOf<Pair<Float, Float>>() }

    LaunchedEffect(Unit) {
    val i = 700
        delay(500)
        chartData.clear()
        val tempList1 = listOf(
            Pair(i.toFloat()+2f, -1f),
            Pair(i.toFloat()+4f, 1f),
            Pair(i.toFloat()+6f, 2f),
            Pair(i.toFloat()+8f, 3f),
            Pair(i.toFloat()+10f, 4f),
            Pair(i.toFloat()+12f, 5f),
        )
        val tempList2 = listOf(
            Pair(i.toFloat()+2f, -10f),
            Pair(i.toFloat()+4f, 10f),
            Pair(i.toFloat()+6f, 20f),
            Pair(i.toFloat()+8f, 30f),
            Pair(i.toFloat()+10f, 40f),
            Pair(i.toFloat()+12f, 50f),
        )
        val tempList3 = listOf(
            Pair(i.toFloat()+2f, -6f),
            Pair(i.toFloat()+4f, 4f),
            Pair(i.toFloat()+6f, 8f),
            Pair(i.toFloat()+8f, 11f),
            Pair(i.toFloat()+10f, 14f),
            Pair(i.toFloat()+12f, 20f),
        )
        chartData.addAll(tempList1)
        chartData2.addAll(tempList2)
        chartData3.addAll(tempList3)
            chartData.forEach { Log.d("Preview", "Chart value -> $it") }

    }
    TemplateTheme {
        MultipleLinesChartView(
            chartData = listOf(chartData, chartData2, chartData3),
        )
    }
}