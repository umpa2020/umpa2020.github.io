package com.umpa2020.tracer.locationBackground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Oreo 이상일 경우 Notification Channel 생성
 */
object LocationNotificationManager {

  val CHANNEL_ID = LocationBackgroundService::class.java.simpleName
  val CHANNEL_NAME = LocationBackgroundService::class.java.name

  var mNotificationManager: NotificationManager? = null

  fun getNotificationId(): String {
    return CHANNEL_ID
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createNotification(context: Context?) {
    val id = CHANNEL_ID
    val name = CHANNEL_NAME
    val impotance = NotificationManager.IMPORTANCE_HIGH
    // depending on the Android API that we're dealing with we will have
    // to use a specific method to create the notification

    val mChannel = NotificationChannel(id, name, impotance)

    mChannel.enableVibration(false)
    mChannel.enableLights(false)

    mNotificationManager =
      context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    mNotificationManager!!.createNotificationChannel(mChannel)
  }
}