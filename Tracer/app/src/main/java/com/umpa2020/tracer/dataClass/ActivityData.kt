package com.umpa2020.tracer.dataClass

import com.google.firebase.firestore.DocumentReference
import com.umpa2020.tracer.network.BaseFB

data class ActivityData(
  val mapId: String? = null,
  val time: Long? = null,
  val distance: Double? = null,
  val playTime: Long? = null,
  var mode: BaseFB.ActivityMode? = null,
  var dataRef: DocumentReference? = null
) 