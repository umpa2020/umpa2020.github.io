package com.umpa2020.tracer.extensions

import android.widget.Toast
import com.umpa2020.tracer.App

fun String.show(){
  val text = this
  val duration = Toast.LENGTH_LONG
  val toast = Toast.makeText(App.instance.context(), text, duration)
  toast.show()

}
fun Int.toRank():String{
  when(this%10){
    1-> return "${this}st"
    2-> return "${this}nd"
    3-> return "${this}rd"
    else-> return "${this}th"
  }
}
