package com.korea50k.RunShare.Activities.Racing

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.korea50k.RunShare.dataClass.ConvertJson
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_running_save.*
import com.korea50k.RunShare.Activities.MainActivity


class RacingFinishActivity : AppCompatActivity() {
    lateinit var runningData:RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing_finish)

        runningData=intent.getSerializableExtra("Running Data") as RunningData
        distance_tv.text=runningData.distance.toString()
        time_tv.text=runningData.time
        speed_tv.text=runningData.speed.toString()
        calorie_tv.text=runningData.cal.toString()
    }

    fun onClick(view: View) {
        when (view.id) {
            com.korea50k.RunShare.R.id.save_btn -> {
                //send runningData to server by json
                runningData.map_title=save_title_edit.text.toString()

                var json = ConvertJson.RunningDataToJson(runningData)
                //send to server

                Log.wtf("wtf",json)

                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)

                startActivity(newIntent)
            }
        }
    }
}
