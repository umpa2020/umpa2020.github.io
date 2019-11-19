package com.superdroid.facemaker.Activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.superdroid.facemaker.R


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