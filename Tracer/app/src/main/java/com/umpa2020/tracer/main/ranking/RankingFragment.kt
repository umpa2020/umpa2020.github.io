package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.ANIMATION_DURATION_TIME
import com.umpa2020.tracer.constant.Constants.Companion.MAX_DISTANCE
import com.umpa2020.tracer.constant.Constants.Companion.MAX_SEEKERBAR
import com.umpa2020.tracer.network.FBRanking
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment() {
  lateinit var location: LatLng
  var distance = MAX_DISTANCE

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

    if (UserInfo.rankingLatLng != null)  {
      FBRanking().getExcuteDESCENDING(requireContext(), view, UserInfo.rankingLatLng!!, "execute")
    }

    //필터 버튼 누르면 레이아웃 보임
    view.rankingToolBarTuneButton.setOnClickListener {
      if(view.tuneLinearLayout.visibility == GONE){
        appearAnimation()
      }
    }

    //필터 레이아웃 밖 영역 클릭하면 사라짐
    view.disappearLayout.setOnClickListener {
      disappearAnimation()
    }

    //기본 값 누를 때
    view.allDeleteButton.setOnClickListener {
      view.tuneRadioBtnExecute.isChecked = true
      view.tuneRadioBtnLike.isChecked = false
      view.progressTextView.text = "100+"
      view.seekBar.progress = MAX_DISTANCE
    }

    //적용 버튼 누를때
    view.applyButton.setOnClickListener {
      val tuneDistance = distance
      Logg.d("ssmm11 tune distance = $tuneDistance")
      if (UserInfo.rankingLatLng != null) {
        //실행순 버튼에 체크가 되어 있을 경우
        if (view.tuneRadioBtnExecute.isChecked) {
          view.rankingfiltermode.text = "실행수"
          FBRanking().getFilterRange(view, UserInfo.rankingLatLng!!, tuneDistance, "execute")
        } else {
          view.rankingfiltermode.text = "좋아요"
          FBRanking().getFilterRange(view, UserInfo.rankingLatLng!!, tuneDistance, "likes")
        }
        disappearAnimation()
      }
    }

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
    return view

  }

  /**
   * 레이아웃이 스르륵 보이는 함수
   */

  fun appearAnimation() {
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
  fun disappearAnimation() {
    requireView().tuneLinearLayout.visibility = View.GONE
    requireView().filterLayout.visibility = View.GONE
    requireView().disappearLayout.visibility = View.GONE

    val animate = AlphaAnimation(1f, 0f)
    animate.duration = ANIMATION_DURATION_TIME
    animate.fillAfter = true
    requireView().tuneLinearLayout.startAnimation(animate)
  }
}
