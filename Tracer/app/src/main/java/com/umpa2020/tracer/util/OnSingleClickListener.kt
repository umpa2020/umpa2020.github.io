package com.umpa2020.tracer.util

import android.os.SystemClock
import android.view.View
import com.umpa2020.tracer.constant.Constants


interface OnSingleClickListener : View.OnClickListener {
  open fun onSingleClick(v: View?):Boolean{
    val currentClickTime = SystemClock.uptimeMillis()
    val elapsedTime = currentClickTime - Constants.mLastClickTime
    Constants.mLastClickTime = currentClickTime

    // 중복클릭 아닌 경우
    if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
      return true
    }
    return false
  }

  override fun onClick(v: View?){
    val currentClickTime = SystemClock.uptimeMillis()
    val elapsedTime = currentClickTime - Constants.mLastClickTime
    Constants.mLastClickTime = currentClickTime

    // 중복클릭 아닌 경우
    if (elapsedTime > Constants.MIN_CLICK_INTERVAL) {
      onSingleClick(v)
    }
  }
}