package com.umpa2020.tracer.roomDatabase.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.roomDatabase.MyRoomDatabase
import com.umpa2020.tracer.roomDatabase.entity.GPSData
import com.umpa2020.tracer.roomDatabase.repository.GpsRepository
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.launch

class GpsViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: GpsRepository
  val allGps: LiveData<GPSData>
  val isUid : LiveData<GPSData>

  init {
    val gpsDao = MyRoomDatabase.getDatabase(application, viewModelScope).gpsDao()
    repository = GpsRepository(gpsDao)
    allGps = repository.allGps
    isUid = repository.isUid

    if(isUid.value==null){
      Logg.d("is uid is null")
    }
  }

  fun insert(gpsData: GPSData) = viewModelScope.launch {
    repository.insert(gpsData)
  }

  fun deleteAll() = viewModelScope.launch {
    repository.deleteAll()
  }

  fun updateLastPosition(lat : Double, lng : Double) = viewModelScope.launch {
    repository.updateLastPosition(lat, lng)
  }

}