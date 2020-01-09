package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.korea50k.RunShare.Activities.Racing.RacingActivity
import com.korea50k.RunShare.R
import com.korea50k.RunShare.Util.ConvertJson
import kotlinx.android.synthetic.main.activity_run_this_map.*

class RunThisMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_this_map)

        run_this_map_race_button.setOnClickListener{
            val assetManager = resources.assets

            //TODO:서버에서 데이터 가져와서 해야함
            val inputStream= assetManager.open("testjson")
            val jsonString = inputStream.bufferedReader().use { it.readText() }


            var newIntent = Intent(this, RacingActivity::class.java)
            newIntent.putExtra("Running data", ConvertJson.JsonToRunningData(jsonString))
            startActivity(newIntent)
        }
    }

    /*
    fun onClick(v: View){
        val assetManager = resources.assets

        //TODO:서버에서 데이터 가져와서 해야함
        val inputStream= assetManager.open("testjson")
        val jsonString = inputStream.bufferedReader().use { it.readText() }


        var newIntent = Intent(this, RacingActivity::class.java)
        newIntent.putExtra("Running data", ConvertJson.JsonToRunningData(jsonString))
        startActivity(newIntent)
    }*/

}
