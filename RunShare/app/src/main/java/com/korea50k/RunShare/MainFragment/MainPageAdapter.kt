package com.korea50k.RunShare.MainFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class MainPageAdapter(fm: FragmentManager, private val mPageCount: Int) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment{
        when (position) {
            0 -> {
                return RankFragment()
            }
            1 -> {
                return MapFragment()
            }
            2 -> {
                return FeedFragment()
            }
        }
        return MapFragment()
    }

    override fun getCount(): Int {
        return mPageCount
    }
}