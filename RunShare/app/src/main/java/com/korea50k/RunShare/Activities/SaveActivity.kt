package com.korea50k.RunShare.Activities

import android.content.Intent
import android.content.Intent.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.korea50k.RunShare.DataClass.RunningData
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_save.*

class SaveActivity : AppCompatActivity() {
    lateinit var runningData:RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)
        runningData=intent.getSerializableExtra("Running Data") as RunningData
        distance_tv.text=runningData.distance.toString()
        time_tv.text=runningData.time
        speed_tv.text=runningData.speed.toString()
        calorie_tv.text=runningData.cal.toString()
    }
    fun onClick(view: View) {
        when (view.id) {
            R.id.save_btn -> {
                //send runningData to server by json
                runningData.map_title=save_title_edit.text.toString()
                var gson=Gson()
                save_text_edit.text=gson.toJson(runningData)

                /*var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
               // newIntent.putExtra("MAP", route_data)
                startActivity(newIntent)*/
            }
        }
    }
}
