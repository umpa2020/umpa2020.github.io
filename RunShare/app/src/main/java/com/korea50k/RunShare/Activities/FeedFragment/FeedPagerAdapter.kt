package com.korea50k.RunShare.Activities.RankFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.korea50k.RunShare.MainFragment.MapFragment

class FeedPagerAdapter(fm: FragmentManager, private val mPageCount: Int) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return  when (position) { //position에 따른 fragment 부르기
            0 -> {
                fragment_feed_users()
            }
            else -> {
                fragment_feed_users()
            }
        }
    }

    override fun getCount(): Int {
        return mPageCount
    }

    override fun getPageTitle(position: Int): CharSequence? { //타이틀 정하는 부분
        return when (position){
            0 -> "racingMap"
            else -> {return "player"}
        }
    }

}