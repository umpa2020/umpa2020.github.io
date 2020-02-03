package com.korea50k.tracer.ranking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.RankRecyclerItemClickItem
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*

class RankRecyclerItemClickActivity : AppCompatActivity() {

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


        //TODO 데이터 서버에서 받아와야 함
        var mdata = arrayListOf<RankRecyclerItemClickItem>(
            RankRecyclerItemClickItem("test1", 111),
            RankRecyclerItemClickItem("test2", 2222),
            RankRecyclerItemClickItem("test3", 3333),
            RankRecyclerItemClickItem("test4", 4444),
            RankRecyclerItemClickItem("test5", 5555),
            RankRecyclerItemClickItem("test6", 6666),
            RankRecyclerItemClickItem("test7", 7777),
            RankRecyclerItemClickItem("test8", 8),
            RankRecyclerItemClickItem("test9", 9),
            RankRecyclerItemClickItem("test10", 10),
            RankRecyclerItemClickItem("test11", 11),
            RankRecyclerItemClickItem("test12", 12)
        )

        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(this)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter = RankRecyclerViewAdapterTopPlayer(mdata)


        rankRecyclerMoreButton.setOnClickListener{
            val nextIntent = Intent(this, RankingMapDetailActivity::class.java)
            nextIntent.putExtra("MapTitle", mapTitle)
            startActivity(nextIntent)
        }
    }
}
