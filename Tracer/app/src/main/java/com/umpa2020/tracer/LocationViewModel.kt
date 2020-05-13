package com.umpa2020.tracer

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umpa2020.tracer.util.Logg
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
  val location = MutableLiveData<Location>()

  fun setLocation(locate : Location) = viewModelScope.launch{
    Logg.d("셋팅 돼?")
    location.value = locate
  }

  fun getLocation(): Location? {
    return location.value
  }
}