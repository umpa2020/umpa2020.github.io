package com.umpa2020.tracer.dataClass

data class ChallengeData (
  var id: String? = null,
  var name: String? = null,
  var date: String? = null,
  var from: String? = null,
  var to: String? = null,
  var locale: String? = null,
  var address: String? = null,
  var host: String? = null,
  var intro: String? = null,
  var raceDesc: String? = null,
  var type: String? = null,
  var link: String? = null,

  var arrCategory: MutableList<String>? = null,
  var gpxPath: String? = null,
  var imagePath: String? = null
)

/*
data class ChallengeData (
  var iconsBar: Int? = null,
  var nametxt: String? = null,
  var datetxt: String? = null,
  var dateweek: String? = null
)
*/
