package com.korea50k.tracer.util

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.korea50k.tracer.R

class ProgressBar(context:Context): AlertDialog(context) {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog)

        
    }
}