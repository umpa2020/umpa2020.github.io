package com.umpa2020.tracer.lockscreen.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.lockscreen.LockScreenActivity

class LockScreenService : Service() {

  private var isPhoneIdleNum: Int? = null

  // by lazy : 호출 시점에 by lazy 정의에 의해서 초기화를 진행한다.
  private val telephonyManager: TelephonyManager?
  // 휴대폰 정보 가져오기 변수 초기화
  // as : 자바에서의 (TelephonyManager)getSystemService와 같은 것.
    by lazy { getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }

  private val lockScreenReceiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
      context?.run {
        when (intent.action) {
          Intent.ACTION_SCREEN_OFF -> { // 스크린이 꺼졌을 때

            telephonyManager?.run {
              // callState : Returns the state of all calls on the device. 음성 통화 상태 조회
              isPhoneIdleNum = callState

            } ?: run {
              getSystemService(TELEPHONY_SERVICE) as TelephonyManager
              // listen(PhoneStateListener listener, int events)
              // Registers a listener object to receive notification of changes in specified telephony states.
              telephonyManager?.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
            }

            // CALL_STATE_IDLE : 통화 끝, 벨소리 울리는 중에 통화 거절
            // Device call state: No activity.
            // Constant Value: 0 (0x00000000)
            if (isPhoneIdleNum == TelephonyManager.CALL_STATE_IDLE) {

              startLockScreenActivity()
            }
          }
        }
      }
    }
  }

  // 통화 상태 얻어오기
  private val phoneListener = object : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
      super.onCallStateChanged(state, incomingNumber)

      isPhoneIdleNum = state
    }
  }

  //
  fun stateReceiver(isStartReceiver: Boolean) {
    if (isStartReceiver) {
      val filter = IntentFilter()
      filter.addAction(Intent.ACTION_SCREEN_OFF)
      registerReceiver(lockScreenReceiver, filter)
    } else {
      unregisterReceiver(lockScreenReceiver)
    }
  }

  private fun createNotificationCompatBuilder(): NotificationCompat.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      val mBuilder = NotificationCompat.Builder(
        this@LockScreenService,
        NotificationManager.getMainNotificationId()
      )
//        .setContentTitle(getString(R.string.gps_activation))
        .setContentTitle(getString(R.string.lockscreen_activation))
        .setSmallIcon(R.mipmap.ic_launcher_tracer_final)
        .setTicker("Ticker text")
        .setAutoCancel(true) // 사용자가 탭하면 자동으로 알림을 삭제하는 setAutoCancel()을 호출
      mBuilder
    } else {

      NotificationCompat.Builder(this@LockScreenService, "")
    }
  }


  override fun onCreate() {
    super.onCreate()


    /**
     *  포그라운드 서비스로 인해 onCreate()에서 알림창 초기화 및 실행.
     */
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // 포그라운드 서비스는 알림창이 있어야 실행 가능.

      // 서비스 id는 0이 아니여야 한다.
      startForeground(
        Constants.FOREGROUND_ID, createNotificationCompatBuilder()
          .build()
      )
    }
  }


  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId) // 이걸 지우면 코드에 노란 박스 사라짐.

    stateReceiver(true)

    return START_STICKY
  }

  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onDestroy() {
    super.onDestroy()

//    NotificationManager.cancelnNotificationChannel(this@LockScreenService)

    // 서비스에 인텐트 action을 줘서 다시 notification 교환.
    // 이때의 startService는 서비스 시작이 아닌 단순 정보 전달.
    // 또한 이때 서비스는 onStartCommand()부터 재시작.
    Intent(App.applicationContext(), LocationBackgroundService::class.java).also {
      it.action = "reStartNotification"
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상부터 foregroundService로 실행.
        startForegroundService(it)
      } else {
        startService(it)
      }
    }
    stateReceiver(false)
  }

  private fun startLockScreenActivity() {
    startActivity(LockScreenActivity.newIntent(this@LockScreenService))
  }
}
