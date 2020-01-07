package com.korea50k.RunShare.Util

import android.util.Log
import com.google.gson.Gson
import com.korea50k.RunShare.dataClass.RankMapData
import com.korea50k.RunShare.dataClass.RunningData
import org.json.JSONObject

class ConvertJson{
    companion object{
        fun RunningDataToJson(runningData: RunningData):String{
            var gson=Gson()
            var jsonString=gson.toJson(runningData)
            return jsonString
        }
        fun JsonToRunningData(json: String): RunningData {
            // var gson = Gson()
            var runningData =Gson().fromJson(json, RunningData::class.java)

            return runningData
        }
        fun RankMapDataToJson(rankmapdata : RankMapData):String{
            var gson=Gson()
            var jsonString=gson.toJson(rankmapdata)
            return jsonString
        }

        fun JsonToRankMapDatas(json: String):ArrayList<RankMapData>{
            // var gson = Gson()
            var rankMapDatas= ArrayList<RankMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")
            var gson=Gson()
            var test = RankMapData()
            test.Excute="3"
            test.Likes="2"
            test.MapTitle="123"
            //test.rank=1
            var tests = ArrayList<RankMapData>()
            tests.add(test)
            tests.add(test)
            tests.add(test)
            Log.d("gsontest", gson.toJson(tests))
            Log.d("asdf", jArray.toString())

            for (i in 0 until jArray.length()) {
                //rankMapDatas.add(Gson().fromJson(gson.toJson(jArray.get(i)), RankMapData::class.java))
                var rankMapData = RankMapData()
                rankMapData.Excute= jArray.getJSONObject(i).get("Excute") as String
                rankMapData.Likes= jArray.getJSONObject(i).get("Likes") as String
//                rankMapData.rank= jArray.getJSONObject(i).get("rank") as Int
                rankMapData.MapTitle= jArray.getJSONObject(i).get("MapTitle") as String

                rankMapDatas.add(rankMapData)

            }
            return rankMapDatas
        }
    }
}