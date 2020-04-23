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

    var challengeData = ChallengeData(0,"1","1","1")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(0,"2","3","4")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(0,"2","3","5")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(0,"2","3","6")
    challengeDatas.add(challengeData)


    view.challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(challengeDatas)
    view.challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
    return view
  }



}