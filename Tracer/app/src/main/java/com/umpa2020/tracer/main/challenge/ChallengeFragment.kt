package com.umpa2020.tracer.main.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.main.challenge.ChallengeLayout

class ChallengeFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)

    return view

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

  }

  val data : ArrayList<ChallengeLayout>
  get()
  {
    val item_liste : ArrayList<ChallengeLayout> = ArrayList<ChallengeLayout>()

    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item1"))
    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item2"))
    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item3"))
    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item4"))
    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item5"))
    item_liste.add(ChallengeLayout(R.mipmap.ic_launcher_tracer, "item6"))

    return item_liste
  }

}