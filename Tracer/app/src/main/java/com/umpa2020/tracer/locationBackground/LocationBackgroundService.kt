package com.umpa2020.tracer.locationBackground

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.RemoteException
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationRequest
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants
import com.umpa2020.tracer.lockscreen.service.NotificationManager
import com.umpa2020.tracer.main.start.running.RunningActivity

/**
 *  IntentService : 오래걸리지만 메인스레드와 관련이 없는 작업을할 때 주로 이용한다.
 *  만약 메인 스레드와 관련된 작업을 해야 한다면 메인스레드 Handler나 Boradcast intent를 이용해야 한다.
 *  출처: https://fullstatck.tistory.com/23 [풀스택 엔지니어]
 */
class LocationBackgroundService : IntentService("LocationBackgroundService"),
  LocationUpdatesComponent.ILocationProvider {

  /**
   * Activity와 Service 통신
   *  1. Activity는 Service에 이벤트를 전달
   *  2. Service는 이벤트를 받고 어떤 동작을 수행
   *  3. Service는 Activity로 결과 이벤트를 전달
   */
  // notification을 클릭하면 지정한 액티비티로 이동.

  override fun onCreate() {
    super.onCreate()

    val pendingIntent: PendingIntent =
      Intent(this, RunningActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(this, 0, notificationIntent, 0)
      }
    /**
     *  위치 관련 생성.
     */

    LocationUpdatesComponent.setILocationProvider(this)
    LocationUpdatesComponent.onCreate(this)
    LocationUpdatesComponent.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    startForegroundNotification()
  }

  /**
   *  포그라운드 서비스로 인해 onCreate()에서 알림창 초기화 및 실행.
   */
  private fun startForegroundNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // 포그라운드 서비스는 알림창이 있어야 실행 가능.
      // 채널 생성은 한번만 해도 됌.
      NotificationManager.createMainNotificationChannel(this@LocationBackgroundService)

      startForeground(
        Constants.FOREGROUND_ID, createNotificationCompatBuilder()
          .build()
      )
    }
  }

  // this makes service running continuously,commenting this start command method service runs only once
  // action에 따른 service 실행 유무
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


    if (intent!!.action == "reStartNotification") {
      startForegroundNotification()
    } else {
      val action = intent.action

      when (action) {
        ServiceStatus.START.name -> startService()
        ServiceStatus.STOP.name -> stopService()
      }
    }
    return START_STICKY
  }


  override fun onHandleIntent(intent: Intent?) {

  }

  /**
   * send message by using messenger
   *
   * @param messageID
   * 액티비티에 브로드캐스트를 이용해 message 보내기.
   */

  private fun sendMessage(location: Location) {
    try {
      val intent = Intent("custom-event-name")
      intent.putExtra("message", location)

      LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    } catch (e: RemoteException) {

    }
  }

  /**
   *  LocationComponent에서 locatoin결과 값 받아옴.
   *  => 이걸 이제 액티비티 or 프래그먼트에 전달해주는 것.
   */
  //it의 경우는 함수의 변수가 한 개여야 만 허용.
  override fun onLocationUpdated(location: Location?) {
    location?.let { sendMessage(it) }
  }

  // 알림의 콘텐츠와 채널 설정
  private fun createNotificationCompatBuilder(): NotificationCompat.Builder {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val mBuilder = NotificationCompat.Builder(
        this@LocationBackgroundService,
        NotificationManager.getMainNotificationId()
      )
        .setContentTitle(getString(R.string.gps_activation))
//        .setContentIntent(pendingIntent)
        .setSmallIcon(R.mipmap.ic_launcher_tracer_final)
        .setTicker("Ticker text")
        .setAutoCancel(true) // 사용자가 탭하면 자동으로 알림을 삭제하는 setAutoCancel()을 호출
      mBuilder
    } else {
      NotificationCompat.Builder(this@LocationBackgroundService, "")
    }
  }

  private fun startService() {
    //hey request for location updates
    LocationUpdatesComponent.onStart()
  }

  private fun stopService() {
    LocationUpdatesComponent.onStop()
    stopForeground(true)
    stopSelf() // 이건 뭐지??
  }
}
