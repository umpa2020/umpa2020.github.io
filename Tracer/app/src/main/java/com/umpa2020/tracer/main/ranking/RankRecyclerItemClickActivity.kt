package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.dataClass.LikedMapData
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.extensions.m_s
import com.umpa2020.tracer.extensions.prettyDistance
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankRecyclerItemClickActivity : AppCompatActivity(), OnSingleClickListener {
  lateinit var progressbar: ProgressBar
  val activity = this
  var likes = 0
  var mapTitle = ""
  var mapId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)
    progressbar = ProgressBar(this)
    rankRecyclerMoreButton.setOnClickListener(this)
    rankRecyclerHeart.setOnClickListener(this)

    progressbar.show()
    val intent = intent
    //전달 받은 값으로 Title 설정
    mapId = intent.extras?.getString("mapId").toString()
    mapTitle = mapId.subSequence(0, mapId.length - TIMESTAMP_LENGTH).toString()

    MainScope().launch {
      withContext(Dispatchers.IO) {
        FBMapRepository().getMapInfo(mapId)
      }?.let {
        it.makerId?.let {
          FBProfileRepository().getUserNickname(it).let {
            rankRecyclerNickname.text = it
          }
          FBProfileRepository().getProfileImage(it)?.let {
            rankRecyclerProfileImage.image(it)
          }
        }

      }
    }

    rankRecyclerMapTitle.text = mapTitle

    // 맵 이미지 DB에서 받아와서 설정
    val imageView = rankRoutePriview
    FBImageRepository().getMapImagePath(imageView, mapId)

    // 해당 맵 좋아요 눌렀는지 확인
    FBLikesRepository().getMapLike(mapId, likedMapListener)

    FBMapRankingRepository().listMapRanking(mapId, mapRankingListener)

  }

  override fun onResume() {
    super.onResume()
    Logg.d("onResume()")
  }

  override fun onPause() {
    super.onPause()
    Logg.d("onPause()")
  }

  override fun onDestroy() {
    super.onDestroy()
    Logg.d("onDestroy()")
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.rankRecyclerMoreButton -> {
        val nextIntent = Intent(App.instance.context(), RankingMapDetailActivity::class.java)
        nextIntent.putExtra("mapId", mapId)
        startActivity(nextIntent)
      }
      R.id.rankRecyclerHeart -> {
        if (rankRecyclerHeartSwitch.text == "off") {
          FBLikesRepository().updateLikes(mapId, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
          rankRecyclerHeartSwitch.text = "on"
          likes++
          rankRecyclerHeartCount.text = likes.toString()
        } else if (rankRecyclerHeartSwitch.text == "on") {
          FBLikesRepository().updateNotLikes(mapId, likes)
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
          rankRecyclerHeartSwitch.text = "off"
          likes--
          rankRecyclerHeartCount.text = likes.toString()
        }
      }
    }
  }

  /**
   * 먼저 현재 사용자가 좋아요를 눌렀는지 서버에서 받아온 뒤,
   * 핸들러로 값을 받아 화면에 표시한 후,
   * (이 작업이 조금 오래걸려서 프로그래스바를 여기서 dismiss)
   */

  private val likedMapListener = object : LikedMapListener {
    override fun likedList(likedMaps: List<LikedMapData>) {
      TODO("Not yet implemented")
    }

    override fun liked(liked: Boolean, getlikes: Int) {
      rankRecyclerHeartCount.text = getlikes.toString()
      //adpater 추가
      likes = getlikes
      if (liked) {
        rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
        rankRecyclerHeartSwitch.text = "on"
      } else {
        rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        rankRecyclerHeartSwitch.text = "off"
      }
      progressbar.dismiss()
    }
  }

  private val mapRankingListener = object : MapRankingListener {
    override fun getMapRank(arrRankingData: ArrayList<RankingData>) {
      //레이아웃 매니저 추가
      rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(activity)
      //adpater 추가
      rankRecyclerItemClickRecyclerView.adapter =
        RankRecyclerViewAdapterTopPlayer(arrRankingData, mapId)
    }
  }
}
