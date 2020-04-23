package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.main.challenge.ChallengeRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_challenge.view.*

class ChallengeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment

    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)
    val challengeDatas = arrayListOf<ChallengeData>()

    var challengeData = ChallengeData(R.drawable.button_background,"제주 그란폰도","2019. 04. 01","제주")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"철원 DMZ 랠리","2019. 07. 03","강원")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"양양 그란폰도","2019. 08. 21","경기")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"화천 DMZ 랠리","2019. 06. 21","강원")
    challengeDatas.add(challengeData)


    view.challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(challengeDatas)
    view.challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)

    return view
  }
}