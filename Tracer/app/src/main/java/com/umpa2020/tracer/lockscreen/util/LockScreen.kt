package com.umpa2020.tracer.lockscreen.util

import android.content.Intent
import android.os.Build
import com.umpa2020.tracer.App
import com.umpa2020.tracer.lockscreen.service.LockScreenService


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

    App.applicationContext()?.run {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(Intent(this, LockScreenService::class.java))

      } else {
        startService(Intent(this, LockScreenService::class.java))

      }
    }
  }

  fun deActivate() {
    App.applicationContext()?.run {
      stopService(Intent(this, LockScreenService::class.java))
    }
  }
}