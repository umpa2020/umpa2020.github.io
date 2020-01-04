package com.korea50k.RunShare.Activities.RankFragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.korea50k.RunShare.R

/**
 * A simple [Fragment] subclass.
 */
class fragment_feed_users : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_rank_player, container, false)
    }


}
