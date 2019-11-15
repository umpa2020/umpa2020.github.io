package com.superdroid.facemaker.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.superdroid.facemaker.R

class StopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)

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
