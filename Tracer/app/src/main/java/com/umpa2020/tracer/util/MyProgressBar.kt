package com.umpa2020.tracer.util

import android.app.Activity
import com.umpa2020.tracer.App

class MyProgressBar {
  var switch = 0
  var mprogressBar = ProgressBar(App.instance.currentActivity() as Activity)

  fun progressBarShow(count: Int) {
    switch = count
    mprogressBar.show()
  }

  fun progressBarDismiss() {
    switch--
    if (switch == 0)
      mprogressBar.dismiss()
  }
}