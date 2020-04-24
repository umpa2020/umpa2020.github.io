package com.umpa2020.tracer.main.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.AdChallengeData
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.util.Logg
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*

class ChallengeFragment : Fragment() {

  var adChallengeList = ArrayList<AdChallengeData>()
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)

    adChallengeList.add(AdChallengeData("test", R.drawable.ic_checkpoint_red))
    adChallengeList.add(AdChallengeData("test2", R.drawable.ic_racing_finishpoint))
    adChallengeList.add(AdChallengeData("test2", R.drawable.ic_checkpoint_gray))
    adChallengeList.add(AdChallengeData("test2", R.drawable.ic_racing_startpoint))
    view.adChallengeScrollViewPager.adapter = AdChallengePageAdapter(adChallengeList, requireContext())
    view.adChallengeScrollViewPager.startAutoScroll()
    //view.adChallengeCountTextView.text="${view.adChallengeScrollViewPager.currentItem}/${adChallengeList.size}"
    val challengeDatas = arrayListOf<ChallengeData>()
    val a=view.adChallengeScrollViewPager.currentItem
    view.adChallengeScrollViewPager.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
      adChallengeCountTextView.text="${(view.adChallengeScrollViewPager.currentItem-a)%adChallengeList.size+1}/${adChallengeList.size}"
    }
    var challengeData = ChallengeData(R.drawable.button_background,"제주 그란폰도","2019. 04. 01","제주")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"철원 DMZ 랠리","2019. 07. 03","강원")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"양양 그란폰도","2019. 08. 21","경기")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"화천 DMZ 랠리","2019. 06. 21","강원")
    challengeDatas.add(challengeData)

    view.btn_challenge_from.setOnClickListener{

    }

    view.btn_challenge_to.setOnClickListener{

    }

    view.btn_challenge_region.setOnClickListener{

    }

    view.btn_challenge_search.setOnClickListener{

    }
    view.challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(challengeDatas)
    view.challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
    return view
  }
}
