package com.korea50k.tracer.racing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RacingFinishActivity : AppCompatActivity() {

    var start = 0
    var end = 15
    var activity=this
    lateinit var racerData: InfoData
    lateinit var makerData: InfoData

    var mJsonString = ""
    var MapTitle = ""

}