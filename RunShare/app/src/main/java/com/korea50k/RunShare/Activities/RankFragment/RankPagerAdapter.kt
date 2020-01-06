package com.korea50k.RunShare.Activities.RankFragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.korea50k.RunShare.MainFragment.MapFragment

class RankPagerAdapter(fm: FragmentManager, private val mPageCount: Int) :
        FragmentStatePagerAdapter(fm) { //TODO: change Deprecated fun
    override fun getItem(position: Int): Fragment {
       return  when (position) { //position에 따른 fragment 부르기
            0 -> {
                fragment_rank_map()
            }
            else -> {
                fragment_rank_player()
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