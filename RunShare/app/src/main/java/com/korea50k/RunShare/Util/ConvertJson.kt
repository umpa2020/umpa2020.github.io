package com.korea50k.RunShare.Util

import android.util.Log
import com.google.gson.Gson
import com.korea50k.RunShare.dataClass.*
import org.json.JSONObject
import java.lang.Exception

class ConvertJson{
    companion object{
        fun RunningDataToJson(runningData: RunningData):String{
            var gson=Gson()

            var jsonString=gson.toJson(runningData)
            return jsonString
        }
        fun JsonToRunningData(json: String): RunningData {
            // var gson = Gson()
            Log.wtf("json",json)

            var runningData =Gson().fromJson(json, RunningData::class.java)
            return runningData
        }
        fun RankMapDataToJson(rankmapdata : RankMapData):String{
            var gson=Gson()
            var jsonString=gson.toJson(rankmapdata)
            return jsonString
        }

        fun JsonToRankMapDatas(json: String, start : Int, end : Int):ArrayList<RankMapData>{
            var rankMapDatas= ArrayList<RankMapData>()
            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")

            var limit = 0;
            if (jArray.length() < end)
                limit = jArray.length()
            else
                limit = end


            for (i in start until limit) {
                //rankMapDatas.add(Gson().fromJson(gson.toJson(jArray.get(i)), RankMapData::class.java))
                var rankMapData = RankMapData()
                rankMapData.likes= jArray.getJSONObject(i).get("Likes") as String
                rankMapData.id= jArray.getJSONObject(i).get("Id") as String
                rankMapData.mapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                rankMapData.mapImage= jArray.getJSONObject(i).get("MapImage") as String
                rankMapData.execute= jArray.getJSONObject(i).get("Execute") as String
                Log.d("convert json","conver json map title = "+rankMapData.mapTitle)

                rankMapDatas.add(rankMapData)
            }
            return rankMapDatas
        }

        fun JsonToRankDetailMapDatas(json: String, start : Int, end : Int):ArrayList<RankDetailMapData>{
            var rankDetailMapDatas= ArrayList<RankDetailMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")

            var limit = 0
            if (jArray.length() < end)
                limit = jArray.length()
            else
                limit = end

            for (i in start until limit) {
                var rankDetailMapData = RankDetailMapData()
                rankDetailMapData.Id= jArray.getJSONObject(i).get("Id") as String
                try {
                    rankDetailMapData.MapImage= jArray.getJSONObject(i).get("MapImage") as String
                }
                catch (e : Exception) {
                }
                try {
                    rankDetailMapData.ChallengerId =
                        jArray.getJSONObject(i).get("ChallengerId") as String
                    rankDetailMapData.ChallengerTime =
                        jArray.getJSONObject(i).get("ChallengerTime") as String
                }
                catch (e:Exception) {

                }
                rankDetailMapDatas.add(rankDetailMapData)
            }
            return rankDetailMapDatas
        }
        fun JsonToFeedMapDatas(json: String, start : Int, end : Int):ArrayList<FeedMapData>{
            var feedMapDatas= ArrayList<FeedMapData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")
            var limit = 0;
            if (jArray.length() < end)
                limit = jArray.length()
            else
                limit = end


            for (i in start until limit) {
                var feedMapData = FeedMapData()
                feedMapData.Likes= jArray.getJSONObject(i).get("Likes") as String
                feedMapData.MapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                feedMapData.MapImage= jArray.getJSONObject(i).get("MapImage") as String
                feedMapData.Id= jArray.getJSONObject(i).get("Id") as String

                feedMapDatas.add(feedMapData)

            }
            return feedMapDatas
        }
        fun JsonToProfileImageUrl(json:String):String{
            val jObject=JSONObject(json)
            return jObject.get("Id") as String
        }

        fun JsonToUserMapImageData(json: String, start : Int, end : Int):ArrayList<UserMapImageData>{
            var userMapImageDatas = ArrayList<UserMapImageData>()

            val jObject = JSONObject(json)
            val jArray = jObject.getJSONArray("JsonData")
            var limit = 0
            if (jArray.length() < end)
                limit = jArray.length()
            else
                limit = end


            for (i in start until limit) {
                var userMapImageData = UserMapImageData()
                userMapImageData.MapTitle= jArray.getJSONObject(i).get("MapTitle") as String
                userMapImageData.MapImage= jArray.getJSONObject(i).get("MapImage") as String

                userMapImageDatas.add(userMapImageData)

            }
            return userMapImageDatas
        }
    }
}