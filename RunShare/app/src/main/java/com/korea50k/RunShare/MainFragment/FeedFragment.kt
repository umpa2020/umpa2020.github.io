package com.korea50k.RunShare.MainFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.Activities.RankFragment.FeedPagerAdapter
//import com.korea50k.RunShare.dataClass.Rank_Users
import kotlinx.android.synthetic.main.fragment_feed.*
import android.R
import android.view.View.OnTouchListener;
import android.widget.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import android.opengl.ETC1.getWidth
import android.view.MotionEvent


class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(com.korea50k.RunShare.R.layout.fragment_feed, container, false)

        val choice_btn1 = view.findViewById<View>(com.korea50k.RunShare.R.id.feed_choiceoption_button) as Button
        choice_btn1.setOnClickListener{
            fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    //MotionEvent.ACTION_DOWN -> //do something
                }

                return v?.onTouchEvent(event) ?: true
            }
        }
        return view
    }

}