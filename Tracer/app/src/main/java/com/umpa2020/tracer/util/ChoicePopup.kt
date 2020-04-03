package com.umpa2020.tracer.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.running_activity_yesnopopup.*


class ChoicePopup(context: Context, val text: String, val yes: View.OnClickListener, val no: View.OnClickListener) : Dialog(context) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.running_activity_yesnopopup);
    runningActivityPopUpTextView.text = text
    runningActivityYesButton.setOnClickListener(yes)
    runningActivityNoButton.setOnClickListener(no)
  }
}