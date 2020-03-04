package com.umpa2020.tracer.util

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*

class Chart {
    var altsData:List<Double>
    var speedsData:List<Double>
    var chart:CombinedChart
    constructor(alts:List<Double>,speeds:List<Double>,chart:CombinedChart){
        this.altsData=alts
        this.speedsData=speeds
        this.chart=chart
    }

    //TODO: chart 클래스 빼는거 혹은 정리 부탁좀요 어딜 지워야할지 ..
    fun setChart() {    //클래스로 따로 빼야할듯
        var lineChart = chart
        val alts = ArrayList<Entry>()
        val speeds = ArrayList<BarEntry>()

        for (index in altsData.indices) {
            alts.add(Entry(index.toFloat(), altsData[index].toFloat()))
            speeds.add(BarEntry(index.toFloat(), speedsData[index].toFloat()))
        }

        val xAxis = lineChart.xAxis
        xAxis.isEnabled=false
        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.RED
        yLAxis.axisMaximum = altsData.max()!!.toFloat() + 5
        yLAxis.axisMinimum = altsData.min()!!.toFloat() - 5

        val yRAxis = lineChart.axisRight
        yRAxis.textColor = Color.BLUE
        yRAxis.axisMaximum =speedsData.max()!!.toFloat() + 5
        yRAxis.axisMinimum = 0F


        val set = BarDataSet(speeds, "속력")
        set.color = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderColor = Color.parseColor("#FF0000FF") // 파랑
        set.barBorderWidth = 3f
        set.setDrawValues(false)
        set.setDrawIcons(false)
        set.isHighlightEnabled = false
        set.axisDependency = YAxis.AxisDependency.RIGHT

        // 고도 셋팅
        var speedsData = LineData()
        val lineDataSet2 = LineDataSet(alts, "고도")
        lineDataSet2.lineWidth = 1.5f
        lineDataSet2.color = Color.parseColor("#FFFF0000") // 빨강
        lineDataSet2.setDrawHorizontalHighlightIndicator(false)
        lineDataSet2.setDrawHighlightIndicators(false)
        lineDataSet2.setDrawValues(false)
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.setDrawCircleHole(false)
        lineDataSet2.axisDependency = YAxis.AxisDependency.LEFT

        speedsData.addDataSet(lineDataSet2)

        var combinedData = CombinedData()

        combinedData.setData(BarData(set))
        combinedData.setData(speedsData)


        lineChart.isDoubleTapToZoomEnabled = false // 더블 클릭 막기
        lineChart.setDrawGridBackground(false)
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)

        lineChart.data = combinedData
        lineChart.invalidate()
    }
}