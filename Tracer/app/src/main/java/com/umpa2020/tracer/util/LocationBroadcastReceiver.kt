package com.umpa2020.tracer.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.umpa2020.tracer.main.start.BaseRunningActivity

/**
 *  브로드 케스트 리시버 클래스
 *  serevice로부터 신호(GPS)를 받아 인자값으로 들어오는 map에 전달.
 */
class LocationBroadcastReceiver(val activity: BaseRunningActivity) : BroadcastReceiver() {
//  private var recordViewModel: RecordViewModel =
//    ViewModelProvider(activity).get(RecordViewModel::class.java)
//  companion object{
//    var timeId = 0
//  }
  override fun onReceive(context: Context?, intent: Intent?) {
    val message = intent?.getParcelableExtra<Location>("message")
    val currentLocation = message as Location
    activity.updateLocation(currentLocation)
    Logg.d("실행 중")

//    val records = MapRecordData(currentLocation.speed, 0.0, timeId++)
//    recordViewModel.insert(records)
  }
}
