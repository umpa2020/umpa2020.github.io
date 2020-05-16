package com.umpa2020.tracer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.util.Logg


class MyFirebaseMessageService : FirebaseMessagingService() {

  val auth = FirebaseAuth.getInstance()
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    // TODO(developer): Handle FCM messages here.
    Logg.d("From: " + remoteMessage.from)

    // 푸시알림 메시지 분기
    // putDate를 사용했을 때 data 가져오기
    remoteMessage.data.isNotEmpty().let {
      Logg.d("Message data payload: " + remoteMessage.data)
      if (true) {/* 장기 실행 작업으로 데이터를 처리해야하는지 확인*/
        //장기 실행 작업 (10 초 이상)의 경우 Firebase Job Dispatcher를 사용하십시오
//        scheduleJob()
      } else {
        //10 초 이내에 메시지 처리
        handleNow()
      }
    }

    // 메시지에 알림 페이로드가 포함되어 있는지 확인하십시오.
    remoteMessage.notification?.let {
      Logg.d("Message Notification Title:  ${remoteMessage.notification!!.title}")
      Logg.d("Message Notification Body:  ${remoteMessage.notification!!.body}")
      sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
    }
  }

//  //토큰이 변경되었을때 호출
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
//    val uid = auth.currentUser!!.uid
//    val map = mutableMapOf<String, Any>()
//    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
//      pushToken = instanceIdResult.token
//      Logg.d(pushToken.toString())
//      map["pushtoken"] = pushToken!!
//      FirebaseFirestore.getInstance().collection("pushtokens").document(uid).set(map)
//    }
//  }


  private fun handleNow() {
    Logg.d("Short lived task is done.");
  }

//  private fun scheduleJob() { //장기작업인지(10초이상)일때 처리하는 메소드
//    val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//    val myJob = dispatcher.newJobBuilder()
//      .setService(MyJobService::class.java)
//      .setTag("my-job-tag")
//      .build()
//    dispatcher.schedule(myJob)
//  }
  private fun sendNotification(messageTitle: String, messageBody: String) {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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