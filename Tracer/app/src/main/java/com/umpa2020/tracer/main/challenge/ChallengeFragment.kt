package com.umpa2020.tracer.main.challenge

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.App
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.AdChallengeData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.network.ActivityListener
import com.umpa2020.tracer.network.ChallengeDataListener
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChallengeFragment : Fragment() {

  var dateFormat = SimpleDateFormat("dd MMM, YYY", Locale.KOREA)
  var adChallengeList = ArrayList<AdChallengeData>()
  var from = 1546300800000
  var to = 1609372800000

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)
    val now = Calendar.getInstance()

    view.btn_challenge_from.text = from.format()
    view.btn_challenge_to.text = to.format()

    adChallengeList.add(AdChallengeData("test", R.drawable.first_ad_image_isics))
    adChallengeList.add(AdChallengeData("test2", R.drawable.second_ad_image_spartan))
    adChallengeList.add(AdChallengeData("test2", R.drawable.third_image_korea50k))
    adChallengeList.add(AdChallengeData("test2", R.drawable.ic_start_point))
    view.adChallengeScrollViewPager.adapter =
      AdChallengePageAdapter(adChallengeList, requireContext())
    view.adChallengeScrollViewPager.startAutoScroll()
    //view.adChallengeCountTextView.text="${view.adChallengeScrollViewPager.currentItem}/${adChallengeList.size}"


    FBChallengeRepository().listChallengeData(from, to, "전체", challengeDataListener)
    view.challengeAppBarText.setOnClickListener {
      val intent = Intent(context, ChallengeDataSettingActivity::class.java)
      startActivity(intent)
    }

    val a = view.adChallengeScrollViewPager.currentItem
    view.adChallengeScrollViewPager.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      adChallengeCountTextView.text =
        "${(view.adChallengeScrollViewPager.currentItem - a) % adChallengeList.size + 1}/${adChallengeList.size}"
    }

    view.btn_challenge_from.setOnClickListener {
      val datePicker = DatePickerDialog(
        requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
          val selectedDate = Calendar.getInstance()
          selectedDate.set(Calendar.YEAR, year)
          selectedDate.set(Calendar.MONTH, month)
          selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
          btn_challenge_from.text = selectedDate.time.time.format()
          from = selectedDate.time.time
        },
        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
      )
      datePicker.show()
    }

    view.btn_challenge_to.setOnClickListener {
      val datePicker = DatePickerDialog(
        requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
          val selectedDate = Calendar.getInstance()
          selectedDate.set(Calendar.YEAR, year)
          selectedDate.set(Calendar.MONTH, month)
          selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
          btn_challenge_to.text = selectedDate.time.time.format()
          to = selectedDate.time.time
        },
        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
      )
      datePicker.show()
    }

    view.btn_challenge_region.setOnClickListener {
      val regionChoicePopup = RegionChoicePopup(requireContext())
      regionChoicePopup.show()
    }

    view.btn_challenge_search.setOnClickListener {
      FBChallengeRepository().listChallengeData(from, to, "전체", challengeDataListener)

    }

    return view
  }


  private val challengeDataListener = object : ChallengeDataListener {
    override fun challengeDataList(listChallengeData: MutableList<ChallengeData>) {
      Logg.d("ssmm11 data = $listChallengeData")
      challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(listChallengeData)
      challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
    }
  }
}


