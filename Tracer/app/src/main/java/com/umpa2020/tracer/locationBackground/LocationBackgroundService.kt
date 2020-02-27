package com.umpa2020.tracer.locationBackground

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.umpa2020.tracer.MainActivity.Companion.MESSENGER_INTENT_KEY
import com.umpa2020.tracer.R
import com.umpa2020.tracer.start.RunningActivity
import com.umpa2020.tracer.util.LocationUpdatesComponent

/**
 *  IntentService : 오래걸리지만 메인스레드와 관련이 없는 작업을할 때 주로 이용한다.
 *  만약 메인 스레드와 관련된 작업을 해야 한다면 메인스레드 Handler나 Boradcast intent를 이용해야 한다.
 *  출처: https://fullstatck.tistory.com/23 [풀스택 엔지니어]
 */
class LocationBackgroundService : IntentService("LocationBackgroundService"), LocationUpdatesComponent.ILocationProvider {
    /**
     * Activity와 Service 통신
     *  1. Activity는 Service에 이벤트를 전달
     *  2. Service는 이벤트를 받고 어떤 동작을 수행
     *  3. Service는 Activity로 결과 이벤트를 전달
     */
    private var mActivityMessenger: Messenger? = null

    var notification: Notification? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate ")


        /**
         *  위치 관련 생성.
         */


        Log.d(TAG, this.toString())
        LocationUpdatesComponent.setILocationProvider(this)
        LocationUpdatesComponent.onCreate(this)
        LocationUpdatesComponent.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)


        notification = createNotification()
        startForeground(1, notification)
    }

    // this makes service running continuously,,commenting this start command method service runs only once
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand Service started....")
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        }

        var test = intent!!.getStringExtra("test")
        if (test != null) {
            Log.d(TAG, "중간 요청 성공?" + ", " + test)
        }

        if (intent != null) {
            val action = intent.action
            Log.i(TAG, "onStartCommand action $action")
            when (action) {
                ServiceStatus.START.name -> startService()
                ServiceStatus.STOP.name -> stopService()
            }
        }
        return START_STICKY
    }


    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent $intent")
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     * 액티비티에 message 보내기.
     */
    private fun sendMessage(messageID: Int, location: Location) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the BackgroundLocationActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val m = Message.obtain()
        m.what = messageID
        m.obj = location
        try {
            mActivityMessenger!!.send(m)
            // Toast.makeText(applicationContext, "zzz"+"$location", Toast.LENGTH_SHORT).show()
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }

    }

    /**
     *  LocationComponent에서 locatoin결과 값 받아옴.
     *  => 이걸 이제 액티비티 or 프래그먼트에 전달해주는 것.
     */
    //it의 경우는 함수의 변수가 한 개여야 만 허용.
    //TODO: onLocationUpdated 로 변경
    override fun onLocationUpdate(location: Location?) {
        location?.let { sendMessage(LOCATION_MESSAGE, it) }
    }


    /**
     *  foreground에 알림창 만들기.
     */
    private fun createNotification(): Notification {
        val notificationChannelId = LocationBackgroundService::class.java.simpleName

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "Notifications Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Background location service is getting location..."
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, RunningActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Background Service")
            .setContentText("Background location service is getting location...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .build()
    }

    private fun startService() {
        //hey request for location updates
        LocationUpdatesComponent.onStart()
        // Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
    }

    private fun stopService() {
        LocationUpdatesComponent.onStop()
        stopForeground(true)
        stopSelf()
    }

    companion object {
        private val TAG = "zzz"
        const val LOCATION_MESSAGE = 999
    }
}
