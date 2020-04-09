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
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.fragment_ranking.view.*


/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment(), OnSingleClickListener {
    lateinit var location: LatLng
    var distance = MAX_DISTANCE
    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        root = view

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


        view.rankingToolBarTuneButton.setOnClickListener(this)
        view.disappearLayout.setOnClickListener(this)
        view.allDeleteButton.setOnClickListener(this)
        view.applyButton.setOnClickListener(this)


        return view
    }

    override fun onSingleClick(v: View?) {
        when (v!!.id) {
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
                val tuneDistance = distance
                Logg.d("ssmm11 tune distance = $tuneDistance")
                if (UserInfo.rankingLatLng != null) {
                    //실행순 버튼에 체크가 되어 있을 경우
                    if (requireView().tuneRadioBtnExecute.isChecked) {
                        requireView().rankingfiltermode.text = "실행수"
                        FBRanking().getFilterRange(
                            requireView(),
                            UserInfo.rankingLatLng!!,
                            tuneDistance,
                            "execute"
                        )
                    } else {
                        requireView().rankingfiltermode.text = "좋아요"
                        FBRanking().getFilterRange(
                            requireView(),
                            UserInfo.rankingLatLng!!,
                            tuneDistance,
                            "likes"
                        )
                    }
                    disappearAnimation()
                }
            }

        }
    }

    override fun onResume() {
        Logg.d("Ssmm11 onresume")
        if (UserInfo.rankingLatLng != null) {
            FBRanking().getExcuteDESCENDING(
                requireContext(),
                root,
                UserInfo.rankingLatLng!!,
                "execute"
            )
        }
        super.onResume()
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
