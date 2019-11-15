package com.superdroid.facemaker.fragment


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.otto.Subscribe
import com.superdroid.facemaker.Activity.MainActivity
import com.superdroid.facemaker.Activity.MapActivity
import com.superdroid.facemaker.EventBus.Events
import com.superdroid.facemaker.EventBus.GlobalBus
import com.superdroid.facemaker.FormClass.Map
import com.superdroid.facemaker.R
import java.io.*
import kotlin.concurrent.timer

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() ,View.OnClickListener{
    override fun onClick(view: View) {
        when{
            view.id== R.id.btn_start -> start()
        }
    }

    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var map: Map

    var LoadFileName=""
    lateinit var saveFolder:File
    lateinit var storage: FirebaseStorage
    lateinit var mStorageReference:StorageReference

    var route_t=ArrayList<LatLng>()
    lateinit var timer_tv:TextView
    var time=0
    //권한 체크
    lateinit var start_btn: Button
    lateinit var stop_btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalBus.getBus()?.register(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        GlobalBus.getBus()?.unregister(this)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.map_fragment, container,false)

        saveFolder = File((activity as AppCompatActivity).filesDir, "mapdata"); // 저장 경로

/*
        route_t.add(LatLng(37.6196038,127.0586487)) //test route
        route_t.add(LatLng(37.6196161,127.0586498))
        route_t.add(LatLng(37.6196189,127.0586509))
        route_t.add( LatLng(37.6196208,127.0586544))
        route_t.add( LatLng(37.6196211,127.0586565))
        route_t.add( LatLng(37.6196191,127.0586524))
        route_t.add( LatLng(37.6195855,127.0586574))
        route_t.add( LatLng(37.6195646,127.0586523))
        route_t.add( LatLng(37.6195471,127.0586489))
        route_t.add( LatLng(37.6195327,127.0586457))
        route_t.add( LatLng(37.6195217,127.0586441))
        route_t.add( LatLng(37.6195078,127.0586448))*/

        storage = FirebaseStorage.getInstance()      //firebase 가져오기
        mStorageReference = storage.reference

        val smf =childFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = Map(smf, context as Context)
        timer_tv = view.findViewById<TextView>(R.id.timer_tv)
        timer_tv.bringToFront()

        start_btn=view.findViewById(R.id.btn_start)
        start_btn.setOnClickListener(this)
        // Inflate the layout for this fragment
        return view
    }

    @Subscribe
    fun getFileNameBack(e: Events.FileNameBack){
        print_log(e.fileName)
        LoadFileName=e.fileName
        if(LoadFileName.isNotEmpty()) {  //로드가 된 경우
            print_log("Success Load File : " + LoadFileName.toString())
            readFile()      //로드된 파일 읽기
        }
  }

    override fun onResume() {
        super.onResume()
        GlobalBus.getBus()
            ?.post(Events.FileNameRequest())
    }

    private fun readFile() {                //불러온 파일 읽기
        try{
            // var myfile=File(saveFolder,LoadFileName)            //saveFolder에 LoadFileName 파일
            var myfileRef = mStorageReference.child("maps/" + LoadFileName)
            val ONE_MEGABYTE: Long = 1024 * 1024
            var a = myfileRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener {
                    var buf = it.inputStream().bufferedReader()           //버퍼리더
                    var iterator = buf.lineSequence().iterator()          //버퍼리더의 이터레이터
                    while (iterator.hasNext()) {                                         //한줄 단위로 읽고, 줄이 끝나면 종료
                        var s_line = iterator.next()
                        map.route.add(
                            LatLng(
                                s_line.split(",")[0].toDouble(),
                                s_line.split(",")[1].toDouble()
                            )
                        )      //lat,lng 로 저장되어 있기 때문에 , 기준으로 스플릿해서 추가
                    }
                    buf.close()             //버퍼는 언제나 닫아야함 ㅇㅈ?
                    print_log("Success Read File : "+LoadFileName.toString())
                    map.drawRoute()
                }.addOnFailureListener{
                    print_log("Fail Read File : "+LoadFileName.toString())
                }
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun start(){
        var newIntent= Intent(activity,MapActivity::class.java)
        startActivity(newIntent)
    }
    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }


}

