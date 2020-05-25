package com.umpa2020.tracer.lockscreen.util

import android.content.Intent
import android.os.Build
import com.umpa2020.tracer.App
import com.umpa2020.tracer.lockscreen.service.LockScreenService
import com.umpa2020.tracer.util.Logg


/**
 * 잠금화면 Service를 관리하는 Class
 */

object LockScreen {

  /**
   *  run
   *     LockScreenApplication.applicationContext()을 수신객체로 변환하여
   *     블록 안에서 이를 사용
   */
  fun active() {
    Logg.d("LockScreen active()")
    App.applicationContext()?.run {
      Logg.d("서비스 시작 부분")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(Intent(this, LockScreenService::class.java))
        Logg.d("서비스 시작1")
      } else {
        startService(Intent(this, LockScreenService::class.java))
        Logg.d("서비스 시작2")
      }
    }
  }

  fun deActivate() {
    App.applicationContext()?.run {
      stopService(Intent(this, LockScreenService::class.java))
    }
  }
}