package com.umpa2020.tracer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.util.Logg

/**
 *  FCM(Firebase Cloud Messaging)을 받기 위한 서비스
 *  해당 서비스는 앱 다운과 동시에 자동 실행됨.
 *  https://team-platform.tistory.com/15
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessageService : FirebaseMessagingService() {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    // TODO(developer): Handle FCM messages here.
    Logg.d("From: " + remoteMessage.from)

    // 제공되는 기능이 앱 다운 및 첫 실행과 동시에 자동 실행이라 해당 서비스 start,stop 여부가 불명.
    // 그래서 일단 Shared 값으로 메시지만 보이고 안보이도록 설정.
    val prefs = PreferenceManager.getDefaultSharedPreferences(App.instance.context())
    if (prefs.getBoolean("notificationSetting", true)) {
      // 메시지에 알림 페이로드가 포함되어 있는지 확인하십시오.
      remoteMessage.notification?.let {
        Logg.d("Message Notification Title:  ${remoteMessage.notification!!.title}")
        Logg.d("Message Notification Body:  ${remoteMessage.notification!!.body}")
        sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
      }
    }
  }

  //토큰이 변경되었을때 호출
//  override fun onNewToken(token: String) {
//    super.onNewToken(token)
//    //서버로 바뀐토큰 전송
//    // If you want to send messages to this application instance or
//    // manage this apps subscriptions on the server side, send the
//    // Instance ID token to your app server.
//
//    //v17.0.0 이후부터는 onTokenRefresh()-depriciated
////    var pushToken = FirebaseInstanceId.getInstance().token
//    var pushToken: String? = null
//    Logg.d("Refreshed token : $token")
////    val uid = auth.currentUser!!.uid
//    val uid = FirebaseAuth.getInstance().currentUser!!.uid
//    val map = mutableMapOf<String, Any>()
//    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
//      pushToken = instanceIdResult.token
//      Logg.d(pushToken.toString())
//      map["pushtoken"] = pushToken!!
//      FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(map)
//    }
//  }

  private fun sendNotification(messageTitle: String, messageBody: String) {
    val intent = Intent(this, MainActivity::class.java).apply {
      this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    val channelId = getString(R.string.default_notification_channel_id)
    val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notificationBuilder = NotificationCompat.Builder(this, channelId)
      .setSmallIcon(R.mipmap.ic_launcher_tracer_final)
      .setContentTitle(messageTitle)
      .setContentText(messageBody)
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setContentIntent(pendingIntent)

    val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // android Oreo 알림 채널이 필요합니다
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channelName = "TRACER_CHANEL"
      val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
      notificationManager.createNotificationChannel(channel)
    }
    notificationManager.notify(1004/*알림ID */, notificationBuilder.build())
  }
}