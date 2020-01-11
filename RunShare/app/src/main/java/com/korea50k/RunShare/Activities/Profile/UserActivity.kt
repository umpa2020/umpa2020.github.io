package com.korea50k.RunShare.Activities.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.korea50k.RunShare.R
import com.korea50k.RunShare.dataClass.ContentDTO
import kotlinx.android.synthetic.main.activity_user.*
import java.util.ArrayList

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val fragmentAdapter = UserPagerAdapter(supportFragmentManager)
        //TODO 탭뷰 아이콘 바꾸고 싶으면 여기서 추가
        fragmentAdapter.addFragment(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,"Race",FragmentUserRace())
        fragmentAdapter.addFragment(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,"Community",FragmentUserCommunity())
        /*user_viewpager.adapter = fragmentAdapter

        user_viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(user_tabs))

        user_tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab){
                var i = tab.position
                user_viewpager.currentItem = i
                tabIconSelect()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        user_tabs.setupWithViewPager(user_viewpager)
        tabIconSelect()
*/




    }/*
    fun tabIconSelect(){
        //TOdo 탭뷰 아이콘 클릭했을 떄랑 아닐때 이미지 추가하는 부분
        val tabBtnImgOff = intArrayOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)
        val tabBtnImgOn = intArrayOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background)

        for (i in 0..1) {
            val tab = user_tabs.getTabAt(i)!!
            if(tab.isSelected){
                tab.setIcon(tabBtnImgOn[i])
            }
            else{
                tab.setIcon(tabBtnImgOff[i])
            }
        }
    }*/




}
