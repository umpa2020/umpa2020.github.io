package com.korea50k.tracer

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 곡률을 바꿔서 커스터마이징한 바텀 네비게이션 뷰 등록
        val mView = customBottomBar as CurvedBottomNavigationView
        mView.inflateMenu(R.menu.bottom_menu)
        mView.setSelectedItemId(R.id.action_schedules)
        //getting bottom navigation view and attaching the listener
        mView.setOnNavigationItemSelectedListener(this@MainActivity)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val imageView = mainStartImageView
        // 바텀 네비게이션뷰에서 메뉴 클릭했을 때,
        //TODO:해당 프래그먼트 달아주기
        when (item.itemId) {
            R.id.action_favorites -> {
                imageView.visibility = View.GONE
            }
            R.id.action_schedules -> {
                imageView.visibility = View.VISIBLE
            }
            R.id.action_music -> {
                imageView.visibility = View.GONE
            }
        }
        return true
    }
    fun onClick(v: View) {
        when (v.id) {
            R.id.mainTest -> {
                val newIntent = Intent(this, RunningSaveActivity::class.java)
                startActivity(newIntent)
            }
            R.id.mainStartImageView -> {
                //TODO:MAP START
            }
        }
    }
}
