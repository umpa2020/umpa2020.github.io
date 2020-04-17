package com.umpa2020.tracer.util

import android.app.Activity
import com.umpa2020.tracer.App

class MyProgressBar {
  var switch = 0
  var mprogressBar = ProgressBar(App.instance.currentActivity() as Activity)

  fun show() {
    Logg.d("ssmm11 s switch = $switch / ${App.instance.currentActivity()}")

    if(switch==0) mprogressBar.show()
    switch++
    Logg.d("ssmm11 s switch = $switch / ${App.instance.currentActivity()}")

  }

  fun dismiss() {
    Logg.d("ssmm11 d switch = $switch / ${App.instance.currentActivity()}")

    switch--
    if (switch == 0) mprogressBar.dismiss()
    Logg.d("ssmm11 d switch = $switch / ${App.instance.currentActivity()}")

  }
}