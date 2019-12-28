package com.korea50k.RunShare.Activities

import android.content.Intent
import android.content.Intent.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_save.*

class SaveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save)
    }
    fun onClick(view: View) {
        when (view.id) {
            R.id.save_btn -> {
                var newIntent = Intent(this, MainActivity::class.java)
                newIntent.flags= FLAG_ACTIVITY_CLEAR_TOP
                newIntent.addFlags(FLAG_ACTIVITY_SINGLE_TOP)
               // newIntent.putExtra("MAP", route_data)
                startActivity(newIntent)
            }
        }
    }
}
