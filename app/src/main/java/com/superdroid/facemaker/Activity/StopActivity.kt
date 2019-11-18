package com.superdroid.facemaker.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.superdroid.facemaker.FormClass.MyUser
import com.superdroid.facemaker.FormClass.Route
import com.superdroid.facemaker.R

class StopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)
        if(intent.hasExtra("MAP")){
            var route_data=intent.getSerializableExtra("MAP") as Route
            findViewById<TextView>(R.id.test_txt).text=route_data.route+"\n"+route_data.distance+"\n"+route_data.time+"\n"

            findViewById<ImageView>(R.id.map_img).setImageURI(route_data.bitmap.toUri())
        }

    }
    fun onClick(view: View){
        when(view.id){
            R.id.save_btn->{
                var newIntent= Intent(this,MainActivity::class.java)
                startActivity(newIntent)

            }
        }
    }
}
