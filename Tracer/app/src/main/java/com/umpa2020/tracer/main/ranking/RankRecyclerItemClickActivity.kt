package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.TIMESTAMP_LENGTH
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankRecyclerItemClickActivity : AppCompatActivity(), OnSingleClickListener {
  val activity = this
  var likes = 0
  var mapTitle = ""
  var mapId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)
    rankRecyclerMoreButton.setOnClickListener(this)
    rankRecyclerHeart.setOnClickListener(this)

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
    MainScope().launch {
      rankRoutePriview.image(FBMapRepository().getMapImage(mapId))
      setLiked( FBLikesRepository().isLiked(UserInfo.autoLoginKey,mapId),FBLikesRepository().getMapLikes(mapId))
      FBMapRepository().listMapRanking(mapId).let {
        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(activity)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter =
          RankRecyclerViewAdapterTopPlayer(it, mapId)
      }
    }


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
          MainScope().launch { FBLikesRepository().toggleLikes(UserInfo.autoLoginKey, mapId) }
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
          rankRecyclerHeartSwitch.text = "on"
          likes++
          rankRecyclerHeartCount.text = likes.toString()
        } else if (rankRecyclerHeartSwitch.text == "on") {
          MainScope().launch { FBLikesRepository().toggleLikes(UserInfo.autoLoginKey, mapId) }
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

  fun setLiked(liked: Boolean, getlikes: Int) {
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
  }
}
