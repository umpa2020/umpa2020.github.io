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
import com.umpa2020.tracer.network.GetRanking
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
    //TODO: return inflate~~~
    //TODO: Thread 사용하지 말고, 클래스로 빼서 getInfos 처럼 하면 배열이 받아온다는 걸 미리 알 수 있게
    //TODO: activity Created 로 이전
    val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

    GetRanking().getExcuteDESCENDING(context!!, view, UserInfo.rankingLatLng, "execute")

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
      val tuneDistance = Integer.parseInt(view.progressTextView.text.toString())

      //실행순 버튼에 체크가 되어 있을 경우
      if (view.tuneRadioBtnExecute.isChecked) {
        view.rankingfiltermode.text = "실행수"
        GetRanking().getFilterRange(view, UserInfo.rankingLatLng, tuneDistance, "execute")
      } else {
        view.rankingfiltermode.text = "좋아요"
        GetRanking().getFilterRange(view, UserInfo.rankingLatLng, tuneDistance, "likes")
      }

      //TODO : tuneDistance에 거리 값 넣어놨으니 이대로 필터 적용

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
    view!!.tuneLinearLayout.visibility = View.VISIBLE
    view!!.filterLayout.visibility = View.VISIBLE
    view!!.disappearLayout.visibility = View.VISIBLE
    view!!.tuneLinearLayout.startAnimation(animate)
  }

  /**
   * 레이아웃이 스르륵 사라지는 함수
   */
  fun disappearAnimation() {
    view!!.tuneLinearLayout.visibility = View.GONE
    view!!.filterLayout.visibility = View.GONE
    view!!.disappearLayout.visibility = View.GONE

    val animate = AlphaAnimation(1f, 0f)
    animate.duration = 500
    animate.fillAfter = true
    view!!.tuneLinearLayout.startAnimation(animate)
  }
}