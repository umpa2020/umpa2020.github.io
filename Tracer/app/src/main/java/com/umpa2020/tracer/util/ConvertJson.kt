package com.umpa2020.tracer.util

import com.google.gson.Gson
import com.umpa2020.tracer.dataClass.InfoData
import com.umpa2020.tracer.dataClass.RouteData

class ConvertJson {
    companion object {
        fun RouteDataToJson(routeData: RouteData): String {
            var gson = Gson()

            var jsonString = gson.toJson(routeData)
            return jsonString
        }

        fun JsonToRouteData(json: String): RouteData {
            var routeData = Gson().fromJson(json, RouteData::class.java)
            return routeData
        }
    }
}