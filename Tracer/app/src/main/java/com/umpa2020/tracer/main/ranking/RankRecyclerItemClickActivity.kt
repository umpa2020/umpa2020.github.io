package com.umpa2020.tracer.main.ranking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBLikesRepository
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBProfileRepository
import com.umpa2020.tracer.network.FBUsersRepository
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankRecyclerItemClickActivity : BaseActivity(), OnSingleClickListener {
  val activity = this
  var likes = 0
  var mapId = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_rank_recycler_item_click)

    val progressBar = MyProgressBar()
    progressBar.show()

    rankRecyclerMoreButton.setOnClickListener(this)
    rankRecyclerHeart.setOnClickListener(this)

    val intent = intent
    //전달 받은 값으로 Title 설정
    mapId = intent.getStringExtra(MAP_ID)!!

    launch {
      withContext(Dispatchers.IO) {
        FBMapRepository().getMapInfo(mapId)
      }?.let {
        rankRecyclerMapTitle.text = it.mapTitle
        it.makerId.let {
          FBProfileRepository().getUserNickname(it).let {
            rankRecyclerNickname.text = it
          }
          FBProfileRepository().getProfileImage(it)?.let {
            rankRecyclerProfileImage.image(it)
          }
        }
      }
    }

    launch {
      rankRoutePriview.image(FBMapRepository().getMapImage(mapId))
      setLiked(FBLikesRepository().isLiked(UserInfo.autoLoginKey, mapId), FBLikesRepository().getMapLikes(mapId))
      setPlayed(FBUsersRepository().isPlayed(UserInfo.autoLoginKey, mapId), FBMapRepository().getMapPlays(mapId))
      FBMapRepository().listMapRanking(mapId).let {
        //레이아웃 매니저 추가
        rankRecyclerItemClickRecyclerView.layoutManager = LinearLayoutManager(activity)
        //adpater 추가
        rankRecyclerItemClickRecyclerView.adapter =
          RankRecyclerViewAdapterTopPlayer(it, mapId)
        progressBar.dismiss()
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
          launch { FBLikesRepository().toggleLikes(UserInfo.autoLoginKey, mapId) }
          rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
          rankRecyclerHeartSwitch.text = "on"
          likes++
          rankRecyclerHeartCount.text = likes.toString()
        } else if (rankRecyclerHeartSwitch.text == "on") {
          launch { FBLikesRepository().toggleLikes(UserInfo.autoLoginKey, mapId) }
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

  private fun setLiked(liked: Boolean, getLikes: Int) {
    rankRecyclerHeartCount.text = getLikes.toString()
    //adpater 추가
    likes = getLikes
    if (liked) {
      rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_red_24dp)
      rankRecyclerHeartSwitch.text = "on"
    } else {
      rankRecyclerHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp)
      rankRecyclerHeartSwitch.text = "off"
    }
  }


  private fun setPlayed(played: Boolean, getPlays: Int) {
    rankRecyclerExecuteCount.text = getPlays.toString()

    if (played) {
      //rankRecyclerExecute.setColorFilter(Color.GREEN)
      rankRecyclerExecute.setColorFilter(R.color.colorPrimary)
    }
  }
}
