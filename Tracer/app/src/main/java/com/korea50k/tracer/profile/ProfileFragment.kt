package com.korea50k.tracer.profile


import android.content.Intent
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
import com.korea50k.tracer.racing.PracticeRacingActivity
import com.korea50k.tracer.ranking.RankRecyclerViewAdapterMap
import com.korea50k.tracer.util.ProgressBar
import com.korea50k.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import java.text.SimpleDateFormat
import java.util.*

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

        val progressbar = ProgressBar(context!!)
        progressbar.show()

        // 공유 프리페런스에 있는 닉네임을 반영
        val profileNickname = view.findViewById<TextView>(R.id.profileIdTextView)
        profileNickname.text = UserInfo.nickname

        // storage에 profile image 파일이 어디에 있는지 받아옴
        var profileImagePath = "init"
        profileImagePathDownloadThread = Thread(Runnable {
            val db = FirebaseFirestore.getInstance()

            // 총 거리, 총 시간을 구하기 위해서 db에 접근하여 일단 먼저
            // 이용자가 뛴 다른 사람의 맵을 구함
            db.collection("userinfo").document(UserInfo.email).collection("user ran these maps")
                .get()
                .addOnSuccessListener { result ->
                    var sumDistance = 0.0
                    var sumTime = 0.0

                    for (document in result) {
                        sumDistance += document.get("distance") as Double
                        sumTime += document.get("time") as Long
                    }
                    // 구하고 나서 이용자가 만든 맵의 거리와 시간을 더함
                    db.collection("mapInfo").whereEqualTo("makersNickname", UserInfo.nickname)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document2 in result) {
                                sumDistance += document2.get("distance") as Double
                                sumTime += document2.get("time") as Long
                            }

                            // 총 거리와 시간을 띄워줌
                            profileFragmentTotalDistance.text = String.format("%.3f", sumDistance / 1000) + "km"
                            val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
                            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
                            profileFragmentTotalTime.text = formatter.format(Date(sumTime.toLong()))

                        }
                    progressbar.dismiss()
                }

            // storage 에 올린 경로를 db에 저장해두었으니 다시 역 추적 하여 프로필 이미지 반영
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
                    progressbar.dismiss()
                }
                .addOnFailureListener { exception ->
                }
        })
        profileImagePathDownloadThread.start()

        val routeTextView = view.findViewById<TextView>(R.id.profileRouteTextView)
        routeTextView.setOnClickListener {
            val nextIntent = Intent(activity, ProfileRouteActivity::class.java)
            startActivity(nextIntent)
        }

        val recordTextView = view.findViewById<TextView>(R.id.profileRecordTextView)
        recordTextView.setOnClickListener {
            val nextIntent = Intent(activity, ProfileRecordActivity::class.java)
            startActivity(nextIntent)
        }

        return view
    }
}
