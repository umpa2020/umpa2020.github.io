package com.umpa2020.tracer.extensions

import android.widget.Toast
import com.umpa2020.tracer.App

fun String.show(){
  val text = this
  val duration = Toast.LENGTH_LONG
  val toast = Toast.makeText(App.instance.context(), text, duration)
  toast.show()

}