package com.korea50k.RunShare.dataClass

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.json.JSONObject

class ConvertJson{
    companion object{
        fun RunningDataToJson(runningData: RunningData):String{
            var gson=Gson()
            var jsonString=gson.toJson(runningData)
            return jsonString
        }
        fun JsonToRunningData(json: String):RunningData{
           // var gson = Gson()
            var runningData =Gson().fromJson(json,RunningData::class.java)
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
            val jArray = jObject.getJSONArray("sampleData")
            var gson=Gson()
            var test = RankMapData()
            test.execute=3
            test.like=2
            test.name="123"
            test.rank=1
            var tests = ArrayList<RankMapData>()
            tests.add(test)
            tests.add(test)
            tests.add(test)
            Log.d("gsontest", gson.toJson(tests))
            Log.d("asdf", jArray.toString())

            for (i in 0 until jArray.length()) {
                //rankMapDatas.add(Gson().fromJson(gson.toJson(jArray.get(i)), RankMapData::class.java))
                var rankMapData = RankMapData()
                rankMapData.execute= jArray.getJSONObject(i).get("execute") as Int
                rankMapData.like= jArray.getJSONObject(i).get("like") as Int
                rankMapData.rank= jArray.getJSONObject(i).get("rank") as Int
                rankMapData.name= jArray.getJSONObject(i).get("name") as String

                rankMapDatas.add(rankMapData)

            }
            return rankMapDatas
        }
    }
}
