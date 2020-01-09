package com.korea50k.RunShare.Util

import android.util.Log
import com.google.gson.Gson
import com.korea50k.RunShare.dataClass.FeedMapData
import com.korea50k.RunShare.dataClass.RankDetailMapData
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
            var rankMapDatas= ArrayList<RankMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")

            for (i in 0 until jArray.length()) {
                //rankMapDatas.add(Gson().fromJson(gson.toJson(jArray.get(i)), RankMapData::class.java))
                var rankMapData = RankMapData()

                rankMapData.likes= jArray.getJSONObject(i).get("Likes") as String
                rankMapData.id= jArray.getJSONObject(i).get("Id") as String
                rankMapData.mapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                rankMapData.mapImage= jArray.getJSONObject(i).get("MapImage") as String
                rankMapData.execute= jArray.getJSONObject(i).get("Execute") as String
                rankMapDatas.add(rankMapData)

            }
            return rankMapDatas
        }

        fun JsonToRankDetailMapDatas(json: String):ArrayList<RankDetailMapData>{
            var rankDetailMapDatas= ArrayList<RankDetailMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")

            for (i in 0 until jArray.length()) {
                var rankDetailMapData = RankDetailMapData()
                rankDetailMapData.ChallengerId= jArray.getJSONObject(i).get("ChallengerId") as String
                rankDetailMapData.ChallengerTime= jArray.getJSONObject(i).get("ChallengerTime") as String
                rankDetailMapData.Id= jArray.getJSONObject(i).get("Id") as String
                // 맵 게시자의
                rankDetailMapData.MapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                rankDetailMapData.TempData= jArray.getJSONObject(i).get("TempData") as String

                rankDetailMapDatas.add(rankDetailMapData)

            }
            return rankDetailMapDatas
        }

        fun JsonToFeedMapDatas(json: String):ArrayList<FeedMapData>{
            var feedMapDatas= ArrayList<FeedMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")

            for (i in 0 until jArray.length()) {
                var feedMapData = FeedMapData()
                feedMapData.Likes= jArray.getJSONObject(i).get("Likes") as String
                feedMapData.MapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                feedMapData.MapImage= jArray.getJSONObject(i).get("MapImage") as String
                feedMapData.Id= jArray.getJSONObject(i).get("Id") as String

                feedMapDatas.add(feedMapData)

            }
            return feedMapDatas
        }
    }
}