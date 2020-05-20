package com.umpa2020.tracer.main.challenge

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.M_D
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.network.FBChallengeRepository
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

class ChallengeFragment : Fragment(), CoroutineScope by MainScope() {
  companion object {
    const val DEFAULT_START_DATE = 1546300800000
    const val DEFAULT_END_DATE = 1609372800000
    const val DEFAULT_LOCALE = "전국"
  }

  var from = DEFAULT_START_DATE // 경기 시작일 기본 값 (2019년 1월 1일)
  var to = DEFAULT_END_DATE// 경기 종료일 기본 값 (2020년 12월 31일)
  var locale = DEFAULT_LOCALE // 필터링 지역 기본 값

  lateinit var regionChoicePopup: RegionChoicePopup // 지역 선택했을 때 팝업

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_challenge, container, false)
    initView(view)
    /**
     * 배너에 들어가는 이미지와 아이디를 가져오는 리스너
     */
    launch {
      FBChallengeRepository().listChallengeBannerImagePath()?.let {
        adChallengeScrollViewPager.adapter = AdChallengePageAdapter(it, requireContext())
        adChallengeScrollViewPager.startAutoScroll()
        val a = view.adChallengeScrollViewPager.currentItem
        view.adChallengeScrollViewPager.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
          adChallengeCountTextView.text = "${(view.adChallengeScrollViewPager.currentItem - a) % it.size + 1}/${it.size}"
        }
      }
      FBChallengeRepository().listChallengeData(from, to, "전국").let {
        challengeDataList(it!!)
      }
    }

    return view
  }

  private fun initView(layout: View) {
    val now = Calendar.getInstance()
    with(layout) {
      btn_challenge_from.text = from.format(Y_M_D)
      btn_challenge_to.text = to.format(Y_M_D)
      btn_challenge_region.text = locale

      challengeAppBarText.setOnClickListener {
        val intent = Intent(context, ChallengeDataSettingActivity::class.java)
        startActivity(intent)
      }

      /**
       * 경기 시작일 날짜 시작 클릭 리스너
       */
      btn_challenge_from.setOnClickListener {
        val datePicker = DatePickerDialog(
          requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDateFrom = Calendar.getInstance().apply {
              set(Calendar.YEAR, year)
              set(Calendar.MONTH, month)
              set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            btn_challenge_from.text = selectedDateFrom.format(M_D)
            from = selectedDateFrom
          },
          now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
      }

      /**
       * 경기 종료일 날짜 시작 클릭 리스너
       */
      btn_challenge_to.setOnClickListener {
        val datePicker = DatePickerDialog(
          requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDateTo = Calendar.getInstance().apply {
              set(Calendar.YEAR, year)
              set(Calendar.MONTH, month)
              set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            btn_challenge_to.text = selectedDateTo.format(M_D)
            to = selectedDateTo
          },
          now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
      }

      /**
       * 경기 지역 클릭 리스너
       */
      btn_challenge_region.setOnClickListener {
        regionChoicePopup = RegionChoicePopup(requireContext(), View.OnClickListener { localeButton ->
          locale = (localeButton as Button).text.toString()
          btn_challenge_region.text = locale
          regionChoicePopup.dismiss()
        })
        regionChoicePopup.show()
      }

      btn_challenge_search.setOnClickListener {
        launch {
          FBChallengeRepository().listChallengeData(from, to, locale)?.let {
            challengeDataList(it)
          }
        }
      }
    }
  }

  /**
   * 챌린지 프래그먼트 리사이클러 등록하는 리스너
   */
  private fun challengeDataList(listChallengeData: MutableList<ChallengeData>) {
    challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(listChallengeData)
    challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
  }

  override fun onPause() {
    super.onPause()
//    progressBar.dismiss()
    MainScope().cancel()
  }
}


