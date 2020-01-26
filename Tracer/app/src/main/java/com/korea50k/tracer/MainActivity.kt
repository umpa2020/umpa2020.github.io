package com.korea50k.tracer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.korea50k.tracer.Profile.ProfileFragment
import com.korea50k.tracer.Ranking.RankingFragment
import com.korea50k.tracer.Start.StartFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //bottomNavigation 아이템 선택 리스너
    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null//선택된 프래그먼트 저장하는 변수

        when (item.itemId) { //선택된 메뉴에 따라서 선택된 프래그 먼트 설정
            R.id.navigation_start -> selectedFragment = StartFragment()
            R.id.navigation_profile -> selectedFragment = ProfileFragment()
            R.id.navigation_ranking -> selectedFragment = RankingFragment()
        }

        //동적으로 프래그먼트 교체
        supportFragmentManager.beginTransaction().replace(R.id.container,
            selectedFragment!!).commit()

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //선택한 메뉴로 프래그먼트 바꿈
        bottom_navigation.setOnNavigationItemSelectedListener(navListener)
        //회전됐을 때 프래그먼트 유지
        //처음 실행 했을때 초기 프래그먼트 설정
        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId =  R.id.navigation_start
            supportFragmentManager.beginTransaction().replace(R.id.container,
                StartFragment()
            ).commit()
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            /*R.id.mainTest -> {
                val newIntent = Intent(this, RunningSaveActivity::class.java)
                startActivity(newIntent)
            }

             */

        }
    }


}
