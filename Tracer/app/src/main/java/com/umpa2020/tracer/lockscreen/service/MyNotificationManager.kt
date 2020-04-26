package com.umpa2020.tracer.lockscreen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.umpa2020.tracer.App
import com.umpa2020.tracer.util.Logg

/**
 * Oreo 이상일 경우 Notification Channel 생성
 */
object MyNotificationManager {

  private val CHANNEL_ID = "LockScreen Example ID"
  private val CHANNEL_NAME = "LockScreen Example CHANEL"

  var mNotificationManager: NotificationManager? = null

  fun getMainNotificationId(): String {
    return CHANNEL_ID
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createMainNotificationChannel(context: Context?) {
    val id = CHANNEL_ID
    val name = CHANNEL_NAME
    val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
    val mChannel = NotificationChannel(id, name, importance)

    mChannel.enableVibration(false)
    mChannel.enableLights(false)

    mNotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    mNotificationManager!!.createNotificationChannel(mChannel)
  }

  fun cancelnNotificationChannel(context: Context?) {
    Logg.d("실행돼??")
//    mNotificationManager!!.cancel(App.notificationId)
//    NotificationManagerCompat.from(App.instance).cancel(App.notificationId)
    mNotificationManager =
      context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    mNotificationManager!!.cancel(CHANNEL_ID, App.LocationNitificationId)
  }
}