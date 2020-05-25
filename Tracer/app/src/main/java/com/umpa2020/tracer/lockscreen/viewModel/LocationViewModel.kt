package com.umpa2020.tracer.lockscreen.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.dataClass.DistanceTimeData
import com.umpa2020.tracer.dataClass.TimeData
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.launch

/**
 *  잠금화면에서 데이터 표시를 위한 ViewModel
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {
  val distanceSpeed = MutableLiveData<DistanceTimeData>()
  val times = MutableLiveData<TimeData>()

  fun init(recordData: DistanceTimeData, timeData: TimeData) {
    distanceSpeed.value = recordData
    times.value = timeData
  }

  fun setDistanceSpeed(distanceTimeData: DistanceTimeData) = viewModelScope.launch {
    Logg.d("셋팅 돼?")
    distanceSpeed.value = distanceTimeData
  }

  fun setTimes(timeData: TimeData) = viewModelScope.launch {
    times.value = timeData
  }

}