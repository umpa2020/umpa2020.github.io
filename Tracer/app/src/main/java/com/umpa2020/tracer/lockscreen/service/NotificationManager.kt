package com.umpa2020.tracer.lockscreen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Oreo 이상일 경우 Notification Channel 생성
 */
object NotificationManager {

  private val CHANNEL_ID = "TRACER_ID"
  private val CHANNEL_NAME = "TRACER_CHANEL"

  var mNotificationManager: NotificationManager? = null

  fun getMainNotificationId(): String {
    return CHANNEL_ID
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createMainNotificationChannel(context: Context?) {
    val importance = android.app.NotificationManager.IMPORTANCE_HIGH
    val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
      this.enableVibration(false)
      this.enableLights(false)
    }

    // Register the channel with the system
    mNotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    mNotificationManager!!.createNotificationChannel(mChannel)
  }
}