package com.korea50k.tracer.ranking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.RankRecyclerItemClickItem
import com.korea50k.tracer.dataClass.RankingData
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*

class RankRecyclerItemClickActivity : AppCompatActivity() {
    lateinit var mapRankingDownloadThread: Thread
    var arrRankingData: ArrayList<RankingData> = arrayListOf()
    var rankingData = RankingData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_item_click)

        val intent = getIntent()
        //전달 받은 값으로 Title 설정
        var mapTitle = intent.extras?.getString("MapTitle").toString()

        var cutted = mapTitle.split("||")
        rankRecyclerMapTitle.text = cutted[0]

        //TODO:ImageView 에 이미지 박는 코드 (firebase)

        val imageView = rankRoutePriview

        val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
        val mapImageRef = storage.reference.child("mapImage").child(mapTitle)
        mapImageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                Log.d("ssmm11", "이미지 뷰 로드 성공 : "+mapImageRef.downloadUrl)
                Glide.with(this@RankRecyclerItemClickActivity)
                    .load(task.result)
                    .override(1024, 980)
                    .into(imageView)
            } else {
                Log.d("ssmm11", "이미지 뷰 로드 실패")
            }
        }

        mapRankingDownloadThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            db.collection("rankingMap").document(mapTitle).collection("ranking").orderBy("challengerTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        rankingData = document.toObject(RankingData::class.java)
                        arrRankingData.add(rankingData)
                    }
                    //레이아웃 매니저 추가
                    rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(this)
                    //adpater 추가
                    Log.d("ssmm11", "받아옴 ? = "+ arrRankingData)
                    rankRecyclerItemClickRecyclerView.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData)
                }
                .addOnFailureListener { exception ->
                }
        })

        mapRankingDownloadThread.start()

        rankRecyclerMoreButton.setOnClickListener{
            val nextIntent = Intent(this, RankingMapDetailActivity::class.java)
            nextIntent.putExtra("MapTitle", mapTitle)
            startActivity(nextIntent)
        }
    }
}
