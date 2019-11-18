package com.superdroid.facemaker.Activity

import android.app.Activity
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import androidx.core.view.isVisible
import com.superdroid.facemaker.R
import kotlinx.android.synthetic.main.activity_loading.*
import android.content.Intent as Intent1


class LoadingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        startLogo()

    }

    private fun startLogo() {
        val handler = Handler()
        //imageView.visibility = View.INVISIBLE
        handler.postDelayed(Runnable { finish() }, 3000)
    }
}