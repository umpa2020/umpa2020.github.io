package com.umpa2020.tracer.main.ranking

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.network.FBRanking
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment() {
  lateinit var location: Location

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

    FBRanking().getExcuteDESCENDING(requireContext(), view, UserInfo.rankingLatLng, "execute")

    //필터 버튼 누르면 레이아웃 보임
    view.rankingToolBarTuneButton.setOnClickListener {
      appearAnimation()
    }

    //필터 레이아웃 밖 영역 클릭하면 사라짐
    view.disappearLayout.setOnClickListener {
      disappearAnimation()
    }
    /**
     * 수진이가 xml 만들어주면 해당 기능 붙히기
     */

    //전체 삭제 누를 때
    view.allDeleteButton.setOnClickListener {
      view.tuneRadioBtnExecute.isChecked = false
      view.tuneRadioBtnLike.isChecked = false
      view.progressTextView.text = "0"
      view.seekBar.progress = 0
    }

    //적용 버튼 누를때
    view.applyButton.setOnClickListener {
      //TODO : "100+" 값이 들어와서 오류남. 슬라이드에서 값 빼는 걸로 대체 or "100+"을 split해서 해결
      val tuneDistance = Integer.parseInt(view.progressTextView.text.toString())

      //실행순 버튼에 체크가 되어 있을 경우
      if (view.tuneRadioBtnExecute.isChecked) {
        view.rankingfiltermode.text = "실행수"
        FBRanking().getFilterRange(view, UserInfo.rankingLatLng, tuneDistance, "execute")
      } else {
        view.rankingfiltermode.text = "좋아요"
        FBRanking().getFilterRange(view, UserInfo.rankingLatLng, tuneDistance, "likes")
      }
      disappearAnimation()
    }

    view.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        view.progressTextView.text = i.toString()
        if (i == 100) {
          view.progressTextView.text = "100+"
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
    animate.duration = 500
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
    animate.duration = 500
    animate.fillAfter = true
    requireView().tuneLinearLayout.startAnimation(animate)
  }
}