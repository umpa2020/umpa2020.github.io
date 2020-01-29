package com.korea50k.tracer.util

import android.util.Log
import com.google.gson.Gson
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RouteData
import com.korea50k.tracer.dataClass.RunningData
import org.json.JSONObject
import java.lang.Exception

class ConvertJson {
    companion object {
        fun RouteDataToJson(routeData: RouteData): String {
            var gson = Gson()

            var jsonString = gson.toJson(routeData)
            return jsonString
        }

        fun InfoDataToJson(infoData: InfoData): String {
            var gson = Gson()

            var jsonString = gson.toJson(infoData)
            return jsonString
        }

        fun JsonToRouteData(json: String): RouteData {
            var routeData = Gson().fromJson(json, RouteData::class.java)
            return routeData
        }
    }
}