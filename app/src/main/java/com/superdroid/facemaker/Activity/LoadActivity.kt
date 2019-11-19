package com.superdroid.facemaker.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.superdroid.facemaker.R

class LoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
    }

    fun onClick(view: View){
        when(view.id){
            R.id.load_btn->{
                var newintent= Intent(this,MainActivity::class.java)
                startActivity(newintent)
            }

        }
    }
}