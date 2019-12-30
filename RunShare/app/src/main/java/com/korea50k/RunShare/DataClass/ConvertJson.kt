package com.korea50k.RunShare.DataClass

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader

class ConvertJson{
    companion object{
        fun RunningDataToJson(runningData: RunningData):String{
            var gson=Gson()
            var jsonString=gson.toJson(runningData)
            return jsonString
        }
        fun JsonToRunningData(json: String):RunningData{
            var gson = Gson()
            var runningData =Gson().fromJson(json,RunningData::class.java)
            return runningData
        }
    }
}
