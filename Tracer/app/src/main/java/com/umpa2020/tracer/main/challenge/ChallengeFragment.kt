package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R

class ChallengeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)
    return view
  }



}