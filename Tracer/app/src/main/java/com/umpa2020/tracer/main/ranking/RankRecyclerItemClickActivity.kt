package com.umpa2020.tracer.main.ranking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.network.FBLikes
import com.umpa2020.tracer.network.FBMapImage
import com.umpa2020.tracer.network.FBProfile
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*

class RankRecyclerItemClickActivity : AppCompatActivity(), OnSingleClickListener{
  var arrRankingData: ArrayList<RankingData> = arrayListOf()
  var rankingData = RankingData()
  val GETLIKE = 51
  var likes = 0
  var mapTitle=""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)
    rankRecyclerMoreButton.setOnClickListener(this)
    rankRecyclerHeart.setOnClickListener(this)
    val progressbar = ProgressBar(App.instance.currentActivity() as Activity)
    progressbar.show()
    val intent = intent
    //전달 받은 값으로 Title 설정
    mapTitle = intent.extras?.getString("MapTitle").toString()

    val cutted = mapTitle.split("||")
    rankRecyclerMapTitle.text = cutted[0]

    // 맵 이미지 DB에서 받아와서 설정
    val imageView = rankRoutePriview
    FBMapImage().getMapImage(imageView, mapTitle)

    /**
     * 먼저 현재 사용자가 좋아요를 눌렀는지 서버에서 받아온 뒤,
     * 핸들러로 값을 받아 화면에 표시한 후,
     * (이 작업이 조금 오래걸려서 프로그래스바를 여기서 dismiss)
     */
    val mHandler = object : Handler(Looper.getMainLooper()) {
      override fun handleMessage(msg: Message) {
        when (msg.what) {
          GETLIKE -> {
            val getlike = msg.obj as Boolean
            likes = msg.arg1
            progressbar.dismiss()

            rankRecyclerHeartCount.text = likes.toString()
            //adpater 추가
            if (getlike) {
              rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
              rankRecyclerHeartSwitch.text = "on"
            } else {
              rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
              rankRecyclerHeartSwitch.text = "off"
            }
          }
        }
      }
    }

    // 위의 핸들러 코드 사용
    FBLikes().getLike(mapTitle, mHandler)

    val db = FirebaseFirestore.getInstance()

    // 베스트 타임이 랭킹 가지고 있는 것 중에서 이것이 베스트 타임인가를 나타내주는 1,0 값입니다.
    // 그래서 한 사용자의 베스트 타임만 가져오고 또 그것들 중에서 오름차순해서 순위 나타냄
    Logg.d("ssmm11 maptitle = $mapTitle")
    db.collection("rankingMap").document(mapTitle).collection("ranking")
      .whereEqualTo("bestTime", 1)
      .orderBy("challengerTime", Query.Direction.ASCENDING)
      .get()
      .addOnSuccessListener { result ->
        Logg.d("ssmm11 result is empty? = ${result.isEmpty}")
        for (document in result) {
          rankingData = document.toObject(RankingData::class.java)
          arrRankingData.add(rankingData)
        }
        Logg.d("ssmm11, arrRanking = $arrRankingData")
        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(this)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter = RankRecyclerViewAdapterTopPlayer(arrRankingData, mapTitle)
      }
      .addOnFailureListener { exception ->
        Logg.d("ssmm11 exception = ${exception.toString()}")
      }

    db.collection("mapInfo").whereEqualTo("mapTitle", mapTitle)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          // 해당 맵의 메이커 닉네임, 프로필 이미지 주소를 받아온다.
          rankRecyclerNickname.text = document.get("makersNickname") as String
          FBProfile().getProfileImage(rankRecyclerProfileImage, rankRecyclerNickname.text.toString())
        }
      }

  }

  override fun onSingleClick(v: View?) {
    when(v!!.id){
      R.id.rankRecyclerMoreButton->{
        val nextIntent = Intent(App.instance.context(), RankingMapDetailActivity::class.java)
        nextIntent.putExtra("MapTitle", mapTitle)
        startActivity(nextIntent)
      }
      R.id.rankRecyclerHeart->{
        if (rankRecyclerHeartSwitch.text == "off") {
          FBLikes().setLikes(mapTitle, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
          rankRecyclerHeartSwitch.text = "on"
          likes++
        } else if (rankRecyclerHeartSwitch.text == "on") {
          FBLikes().setminusLikes(mapTitle, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
          rankRecyclerHeartSwitch.text = "off"
          likes--
        }
      }
    }
  }
}
