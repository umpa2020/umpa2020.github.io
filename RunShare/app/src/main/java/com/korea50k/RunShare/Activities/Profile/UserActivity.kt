package com.korea50k.RunShare.Activities.Profile

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.Util.S3
import com.korea50k.RunShare.Util.SharedPreValue
import kotlinx.android.synthetic.main.activity_user.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class UserActivity : AppCompatActivity() {
    var activity =this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        var id = intent.getStringExtra("ID")
        //사용자 유저 이름 표시
        profileIdTextView.text = id
        setProfileImage(id)


        val fragmentAdapter = UserPagerAdapter(supportFragmentManager)
        //TODO 탭뷰 아이콘 바꾸고 싶으면 여기서 추가
        fragmentAdapter.addFragment(
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            "Race",
            FragmentUserRace()
        )
        //fragmentAdapter.addFragment(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background,"Community",FragmentUserCommunity())
        user_viewpager.adapter = fragmentAdapter

        user_viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(user_tabs)
        )

        user_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var i = tab.position
                user_viewpager.currentItem = i
//                tabIconSelect()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        user_tabs.setupWithViewPager(user_viewpager)
        tabIconSelect()


    }

    private fun setProfileImage(id: String) {
        Thread(Runnable {
            RetrofitClient.retrofitService.profileImageLinkDownload(id)
                .enqueue(object :
                    retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                       Log.d("server","사용자의 프로필을 불러올수 없습니다")
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        Log.d("server", "success to get profile link")
                        Thread(Runnable {
                            try {
                                val url =
                                    URL("http://15.164.50.86/profileImageLinkDownload.php?" + "Nickname=" + id)
                                Log.d("server URL : ",url.toString())
                                val http = url.openConnection() as HttpURLConnection
                                http.defaultUseCaches = false
                                http.doInput = true
                                http.doOutput = true
                                http.requestMethod = "POST"

                                http.setRequestProperty(
                                    "content-type",
                                    "application/x-www-form-urlencoded"
                                )

                                val buffer = StringBuffer()
                                val outStream = OutputStreamWriter(http.outputStream, "EUC-KR")
                                val writer = PrintWriter(outStream)
                                writer.write(buffer.toString())
                                writer.flush()
                                val tmp = InputStreamReader(http.inputStream, "EUC-KR")
                                val reader = BufferedReader(tmp)
                                val builder = StringBuilder()
                                var line: String? = reader.readLine()
                                while (line != null) {
                                    builder.append(line)
                                    line = reader.readLine()
                                }

                                Log.d("server", builder.toString())
                                var bm= S3.downloadBitmap(builder.toString())
                                activity.runOnUiThread(Runnable {
                                    profileImageView.setImageBitmap(bm)
                                })
                            } catch (e: MalformedURLException) {
                                Log.e("server", e.toString())
                            }
                        }).start()
                    }
                })
        }).start()
    }

    fun tabIconSelect() {
        //TOdo 탭뷰 아이콘 클릭했을 떄랑 아닐때 이미지 추가하는 부분
        val tabBtnImgOff =
            intArrayOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background
            )
        val tabBtnImgOn =
            intArrayOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background
            )
/*
        for (i in 0..1) {
            val tab = user_tabs.getTabAt(i)!!
            if(tab.isSelected){
                tab.setIcon(tabBtnImgOn[i])
            }
            else{
                tab.setIcon(tabBtnImgOff[i])
            }
        }

 */
        //TODO 탭뷰 하나만 있어서
        val tab = user_tabs.getTabAt(0)!!
        if (tab.isSelected) {
            tab.setIcon(tabBtnImgOn[0])
        } else {
            tab.setIcon(tabBtnImgOff[0])
        }
    }


}
