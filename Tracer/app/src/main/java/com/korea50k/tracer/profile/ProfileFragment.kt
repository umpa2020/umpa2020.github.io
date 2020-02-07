package com.korea50k.tracer.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.ranking.RankRecyclerViewAdapterMap
import com.korea50k.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    // firebase DB
    private var mFirestoreDB: FirebaseFirestore? = null

    //  firebase Storage
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null
    lateinit var root: View
    var bundle = Bundle()
    lateinit var profileImagePathDownloadThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         *  Firestore 초기화
         */

        mFirestoreDB = FirebaseFirestore.getInstance()

        Log.d("ssmm11", " sha email" + UserInfo.email)
        Log.d("ssmm11", " shared prefrence " + UserInfo.nickname)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view: View = inflater!!.inflate(R.layout.fragment_profile, container, false)
        root = view
        val fragmentAdapter = UserPagerAdapter(childFragmentManager)

        //아이콘 선택, 비선택 이미지, 타이틀 이름, 추가할 프래그먼트 지정해서 어댑터에 프래그먼트 추가

        fragmentAdapter.addFragment(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, "Route", ProfileUserRouteFragment())
        fragmentAdapter.addFragment(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, "History", ProfileUserHistoryFragment())

        root.profileFragmentViewPager.adapter = fragmentAdapter


        root.profileFragmentViewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(view!!.profileFragmentProfileTabLayout)

        )
        Log.d("profile", "addOnPageChangeListener호출")

        view!!.profileFragmentProfileTabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var i = tab!!.position
                root.profileFragmentViewPager.currentItem = i
                tabIconSelect()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        root.profileFragmentProfileTabLayout.setupWithViewPager(view.profileFragmentViewPager)!!
        // tabIconSelect()


        val profileNickname = view.findViewById<TextView>(R.id.profileIdTextView)
        profileNickname.text = UserInfo.nickname


        var profileImagePath = "init"
        profileImagePathDownloadThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()
            Log.d("ssmm11", "들어옴 = " + UserInfo.nickname)

            db.collection("userinfo").whereEqualTo("nickname", UserInfo.nickname)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        profileImagePath = document.get("profileImagePath") as String
                    }

                    // glide imageview 소스
                    // 프사 설정하는 코드 db -> imageView glide
                    val imageView = view.findViewById<ImageView>(R.id.profileImageView)

                    val storage = FirebaseStorage.getInstance("gs://tracer-9070d.appspot.com/")
                    val mapImageRef = storage.reference.child(UserInfo.nickname).child("Profile").child(profileImagePath)

                    mapImageRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Glide 이용하여 이미지뷰에 로딩
                            Glide.with(this@ProfileFragment)
                                .load(task.result)
                                .override(1024, 980)
                                .into(imageView)
                        } else {
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        })

        profileImagePathDownloadThread.start()



        return view
    }

    fun tabIconSelect() {
        //TOdo 탭뷰 아이콘 클릭했을 떄랑 아닐때 이미지 추가하는 부분
        val tabBtnImgOff =
            intArrayOf(
                R.drawable.ic_launcher_foreground,
                R.drawable.ic_launcher_foreground
            )
        val tabBtnImgOn =
            intArrayOf(
                R.drawable.ic_launcher_background,
                R.drawable.ic_launcher_background
            )

        for (i in 0..1) {
            var tab = root.profileFragmentProfileTabLayout.getTabAt(i)!!
            Log.d("profile", tab.toString())
            if (tab.isSelected) {
                tab.setIcon(tabBtnImgOn[i])
            } else {
                tab.setIcon(tabBtnImgOff[i])
            }
        }

        /*
        //TODO 탭뷰 하나만 있어서
        val tab = user_tabs.getTabAt(0)!!
        if (tab.isSelected) {
            tab.setIcon(tabBtnImgOn[0])
        } else {
            tab.setIcon(tabBtnImgOff[0])
        }
    }

  */

    }
}

