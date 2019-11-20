package com.superdroid.facemaker.Activity

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.superdroid.facemaker.FormClass.Route
import com.superdroid.facemaker.R

class StopActivity : AppCompatActivity() {
    var TAG = "WSY"

    lateinit var route_data : Route

    lateinit var map_title_edit : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)
        map_title_edit =   findViewById<EditText>(R.id.map_title_edit) as EditText
        if(intent.hasExtra("MAP")){
            route_data=intent.getSerializableExtra("MAP") as Route
            findViewById<ImageView>(R.id.map_img).setImageURI(route_data.bitmap.toUri())
            findViewById<TextView>(R.id.distance_tv).text = route_data.distance.toString()
            findViewById<TextView>(R.id.time_tv).text = route_data.time
        }

    }
    fun onClick(view: View){
        when(view.id){
            R.id.save_btn->{
                route_data.map_title = map_title_edit.text.toString()

                var newIntent= Intent(this,MainActivity::class.java)
                newIntent.putExtra("MAP", route_data)
                startActivity(newIntent)
            }
        }
    }

    fun print_log(text:String){
        Log.d(TAG,text.toString())

    }

}
