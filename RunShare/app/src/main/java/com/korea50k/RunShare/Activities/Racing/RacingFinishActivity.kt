package com.korea50k.RunShare.Activities.Racing

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.korea50k.RunShare.dataClass.RunningData
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Activities.MainFragment.MainActivity
import kotlinx.android.synthetic.main.activity_racing_finish.*


class RacingFinishActivity : AppCompatActivity() {
    lateinit var racerData:RunningData
    lateinit var makerData:RunningData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_racing_finish)

        racerData=intent.getSerializableExtra("Racer Data") as RunningData
        makerData=intent.getSerializableExtra("Maker Data") as RunningData
        racerLapTimeTextView.text=racerData.time
        makerLapTimeTextView.text=makerData.time
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.OKButton -> {
                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(newIntent)
            }
        }
    }
}
