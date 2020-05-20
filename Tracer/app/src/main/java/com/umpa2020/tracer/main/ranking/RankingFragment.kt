package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.ANIMATION_DURATION_TIME
import com.umpa2020.tracer.constant.Constants.Companion.MAX_DISTANCE
import com.umpa2020.tracer.constant.Constants.Companion.MAX_SEEKERBAR
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.network.FBRankingRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.MyProgressBar
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_ranking.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment(), OnSingleClickListener, CoroutineScope by MainScope() {
  lateinit var location: LatLng
  lateinit var root: View
  lateinit var rankingRepo: FBRankingRepository

  var rootInfoDatas = arrayListOf<MapInfo>()
  var distance = MAX_DISTANCE
  var tuneDistance = 0
  var isLoding = false
  var limit = 0L

  var rankingLatLng: LatLng? = null

  val progressBar = MyProgressBar()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Shared에서 마지막 위치 가져오기
    val latLng = LatLng(UserInfo.lat.toDouble(), UserInfo.lng.toDouble())
    rankingLatLng = latLng
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_ranking, container, false)
    root = view

    view.rank_recycler_map.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (!rank_recycler_map.canScrollVertically(-1)) {
          // 리사이클러뷰가 맨 위로 이동했을 경우
        } else if (!rank_recycler_map.canScrollVertically(1)) {
          // 리사이클러뷰가 맨 아래로 이동했을 경우
          if (requireView().tuneRadioBtnExecute.isChecked) {
            requireView().rankingfiltermode.text = getString(R.string.execute)
            if (!isLoding) {
              launch {
                progressBar.show()
                rankingRepo.listFilterRange(rankingLatLng!!, tuneDistance, "plays", limit).let {
                  getRank(it, "plays")
                }
              }
            }
          } else {
            requireView().rankingfiltermode.text = getString(R.string.likes)
            if (!isLoding) {
             launch {
                progressBar.show()
                rankingRepo.listFilterRange(rankingLatLng!!, tuneDistance, "likes", limit).let {
                  getRank(it, "likes")
                }
              }
            }
          }
          isLoding = true
        }
      }
    })

    view.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        distance = i

        view.progressTextView.text = distance.toString()
        if (i == MAX_SEEKERBAR) {
          view.progressTextView.text = "100+"
          distance = MAX_DISTANCE
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {}

      override fun onStopTrackingTouch(seekBar: SeekBar) {}
    })

    view.filterLayout.setOnClickListener(this)
    view.rankingToolBarTuneButton.setOnClickListener(this)
    view.disappearLayout.setOnClickListener(this)
    view.allDeleteButton.setOnClickListener(this)
    view.applyButton.setOnClickListener(this)

    return view
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      R.id.filterLayout -> { // 뒤 쪽 클릭 안되게 클릭 리스너 달아서 제한
      }
      R.id.rankingToolBarTuneButton -> { //필터 버튼 누르면 레이아웃 보임
        if (requireView().tuneLinearLayout.visibility == GONE) {
          appearAnimation()
        }
      }
      R.id.disappearLayout -> { //필터 레이아웃 밖 영역 클릭하면 사라짐
        disappearAnimation()
      }
      R.id.allDeleteButton -> { //기본 값 누를 때
        requireView().tuneRadioBtnExecute.isChecked = true
        requireView().tuneRadioBtnLike.isChecked = false
        requireView().progressTextView.text = "100+"
        requireView().seekBar.progress = MAX_DISTANCE
      }

      R.id.applyButton -> { //적용 버튼 누를때
        tuneDistance = distance
        rankingRepo = FBRankingRepository()
        rootInfoDatas.clear()

        if (rankingLatLng != null) {
          //실행순 버튼에 체크가 되어 있을 경우
          if (requireView().tuneRadioBtnExecute.isChecked) {
            requireView().rankingfiltermode.text = getString(R.string.execute)

           launch {
              progressBar.show()
              rankingRepo.listRanking(rankingLatLng!!, tuneDistance, "plays", limit).let {
                getRank(it, "plays")
              }
            }
          } else {
            requireView().rankingfiltermode.text = getString(R.string.likes)

           launch {
              progressBar.show()
              rankingRepo.listRanking(rankingLatLng!!, tuneDistance, "likes", limit).let {
                getRank(it, "likes")
              }
            }
          }
          disappearAnimation()
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (rankingLatLng != null) {
      tuneDistance = distance
      limit = rootInfoDatas.size.toLong()

      if (limit == 0L) limit = 20L
      else rootInfoDatas.clear()

      Logg.d("왜 안나와~~~1")
      if (rankingLatLng != null) {
        //실행순 버튼에 체크가 되어 있을 경우
        if (requireView().tuneRadioBtnExecute.isChecked) {
          requireView().rankingfiltermode.text = getString(R.string.execute)
          rankingRepo = FBRankingRepository()

          Logg.d("왜 안나와~~~2")

         launch {
            Logg.d("왜 안나와~~~3")
            progressBar.show()
            rankingRepo.listRanking(rankingLatLng!!, tuneDistance, "plays", limit).let {
              getRank(it, "plays")
            }
          }
        } else {
          requireView().rankingfiltermode.text = getString(R.string.likes)

          launch {
            progressBar.show()
            rankingRepo.listRanking(rankingLatLng!!, tuneDistance, "likes", limit).let {
              getRank(it, "likes")
            }
          }
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    // 갑자기 뒤로가면 코루틴 취소
    MainScope().cancel()

  }

  /**
   * 레이아웃이 스르륵 보이는 함수
   */

  private fun appearAnimation() {
    val animate = AlphaAnimation(0f, 1f) //투명도 변화
    animate.duration = ANIMATION_DURATION_TIME
    animate.fillAfter = true
    requireView().tuneLinearLayout.visibility = View.VISIBLE
    requireView().filterLayout.visibility = View.VISIBLE
    requireView().disappearLayout.visibility = View.VISIBLE
    requireView().tuneLinearLayout.startAnimation(animate)
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  private fun disappearAnimation() {
    requireView().tuneLinearLayout.visibility = View.GONE
    requireView().filterLayout.visibility = View.GONE
    requireView().disappearLayout.visibility = View.GONE

    val animate = AlphaAnimation(1f, 0f)
    animate.duration = ANIMATION_DURATION_TIME
    animate.fillAfter = true
    requireView().tuneLinearLayout.startAnimation(animate)
  }

  fun getRank(mapInfos: MutableList<MapInfo>, mode: String) {
    progressBar.dismiss()

    rootInfoDatas.addAll(mapInfos)
    if (rootInfoDatas.isEmpty()) {
      rankingRecyclerRouteisEmpty.visibility = View.VISIBLE
    } else {
      //레이아웃 매니저, 어댑터 추가
      if (rootInfoDatas.size < 21) {
        rank_recycler_map.layoutManager = LinearLayoutManager(context)
        rank_recycler_map.adapter = MapRankingAdapter(rootInfoDatas, mode)
      } else {
        rank_recycler_map.adapter!!.notifyDataSetChanged()
      }
      isLoding = false
    }
  }

}
