package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*

class RankRecyclerItemClickActivity : AppCompatActivity() {
  var arrRankingData: ArrayList<RankingData> = arrayListOf()
  var rankingData = RankingData()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)

    val progressbar = ProgressBar(this)
    progressbar.show()
    val intent = intent
    //전달 받은 값으로 Title 설정
    val mapTitle = intent.extras?.getString("MapTitle").toString()

    val cutted = mapTitle.split("||")
    rankRecyclerMapTitle.text = cutted[0]


    val imageView = rankRoutePriview

    val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
    val mapImageRef = storage.reference.child("mapImage").child(mapTitle)
    mapImageRef.downloadUrl.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Glide 이용하여 이미지뷰에 로딩
        Glide.with(this@RankRecyclerItemClickActivity)
          .load(task.result)
          .override(1024, 980)
          .into(imageView)
        progressbar.dismiss()

      }
    }

    val db = FirebaseFirestore.getInstance()

    // 베스트 타임이 랭킹 가지고 있는 것 중에서 이것이 베스트 타임인가를 나타내주는 1,0 값입니다.
    // 그래서 한 사용자의 베스트 타임만 가져오고 또 그것들 중에서 오름차순해서 순위 나타냄
    db.collection("rankingMap").document(mapTitle).collection("ranking")
      .whereEqualTo("bestTime", 1)
      .orderBy("challengerTime", Query.Direction.ASCENDING)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          rankingData = document.toObject(RankingData::class.java)
          arrRankingData.add(rankingData)
        }
        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(this)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData)
      }
      .addOnFailureListener { exception ->
      }

    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          rankRecyclerNickname.text = document.get("makersNickname") as String
        }
      }

    rankRecyclerMoreButton.setOnClickListener {
     /* val nextIntent = Intent(this, RankingMapDetailActivity::class.java)
      nextIntent.putExtra("MapTitle", mapTitle)
      startActivity(nextIntent)*/
    }
  }
}
