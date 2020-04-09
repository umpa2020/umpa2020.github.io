package com.umpa2020.tracer.util

import android.app.Activity
import com.umpa2020.tracer.App

class MyProgressBar {
  var switch = 0
  var mprogressBar = ProgressBar(App.instance.currentActivity()!!)

  fun progressBarShow() {
    if(switch==0) mprogressBar.show()
    switch++
  }

  fun progressBarDismiss() {
    switch--
    if (switch == 0) mprogressBar.dismiss()
  }
}