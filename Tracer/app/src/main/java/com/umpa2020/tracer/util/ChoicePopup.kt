package com.umpa2020.tracer.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.service.autofill.TextValueSanitizer
import android.view.View
import android.view.Window
import android.widget.TextView
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import kotlinx.android.synthetic.main.running_activity_yesnopopup.*

/**
 *  text에는 제목도
 */
class ChoicePopup(context: Context,
                  private val titleText:String,
                  private val bodyText:String,
                  private val yes:View.OnClickListener,
                  private val no:View.OnClickListener) : Dialog(context){
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.running_activity_yesnopopup)
    runningActivityPopUpTitle.text = titleText
    runningActivityPopUpTextView.text=bodyText
    runningActivityYesButton.setOnClickListener(yes)
    runningActivityNoButton.setOnClickListener(no)
  }
}