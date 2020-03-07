package com.umpa2020.tracer.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.umpa2020.tracer.R

class ProgressBar(context:Context): AlertDialog(context) {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}