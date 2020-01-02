package com.korea50k.RunShare.Activities

import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.korea50k.RunShare.DataClass.ConvertJson
import com.korea50k.RunShare.DataClass.RunningData
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_save.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import android.graphics.Color
import android.net.Uri
import androidx.core.net.toUri
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.activity_save.view.*
import java.net.URI


class SaveActivity : AppCompatActivity() {
    lateinit var runningData:RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)

        runningData=intent.getSerializableExtra("Running Data") as RunningData
        map_img.setImageURI(runningData.bitmap.toUri())
        distance_tv.text=runningData.distance.toString()
        time_tv.text=runningData.time
        speed_tv.text=runningData.speed.toString()
        calorie_tv.text=runningData.cal.toString()
        setChart()
    }

    private fun setChart() {
        var lineChart = chart as LineChart

        val entries = ArrayList<Entry>()
        for(alts in runningData.alts.indices){
            entries.add(Entry(alts.toFloat(), runningData.alts[alts].toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "속성명1")
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.parseColor("#FFA1B4DC")
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.getAxisLeft()
        yLAxis.textColor = Color.BLACK

        val yRAxis = lineChart.getAxisRight()
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description()
        description.text = ""

        lineChart.isDoubleTapToZoomEnabled = false;
        lineChart.setDrawGridBackground(false)
        lineChart.description = description
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)
        lineChart.invalidate()
    }

    fun onClick(view: View) {
        when (view.id) {
            com.korea50k.RunShare.R.id.save_btn -> {
                //send runningData to server by json
                runningData.map_title=save_title_edit.text.toString()

                var json = ConvertJson.RunningDataToJson(runningData)
                //send to server!!

                Log.wtf("wtf",json)
                /*
                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)

                startActivity(newIntent)*/
            }
        }
    }
}
