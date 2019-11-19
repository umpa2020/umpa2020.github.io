package com.superdroid.facemaker.Activity

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.superdroid.facemaker.FormClass.Map
import com.superdroid.facemaker.FormClass.Route
import com.superdroid.facemaker.R
import java.io.*
import kotlin.concurrent.timer

class MapActivity:AppCompatActivity(){
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var map: Map
    var sec=0
    var LoadFileName=""
    lateinit var saveFolder: File
    lateinit var storage: FirebaseStorage
    lateinit var mStorageReference: StorageReference

    var route_t=ArrayList<LatLng>()
    lateinit var timer_tv: TextView
    var time=0
    //권한 체크
    lateinit var start_btn: Button
    lateinit var stop_btn: Button

    var route_save=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

       saveFolder = File(filesDir, "mapdata"); // 저장 경로
        storage = FirebaseStorage.getInstance()      //firebase 가져오기
        mStorageReference = storage.reference

        val smf =supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        map = Map(smf, this)
        timer_tv = findViewById<TextView>(R.id.timer_tv)
        timer_tv.bringToFront()

        map.startTracking()
        var timerTask = timer(period=1000){
            time++
            sec= time
            runOnUiThread{
                timer_tv.text=sec.toString()
            }
        }
        var stop_btn=findViewById(R.id.btn_stop) as Button
        stop_btn.setOnLongClickListener{
            var route_data=Route()
            route_data.route=route_save
            route_data.distance=map.getDistance(map.route)
            route_data.time=sec
            map.CaptureMapScreen(route_data)
           /* if (!saveFolder.exists()) {       //폴더 없으면 생성
                saveFolder.mkdir()
            }
            try {
                val path = "map" + saveFolder.list().size + ".txt"        //파일명 생성하는건데 수정필요

                var myfile = File(saveFolder, path)                //로컬에 파일저장
                var buf = BufferedWriter(FileWriter(myfile, true))
                buf.append(route_save);
                buf.close();

                val stream = FileInputStream(myfile)        //서버에 파일저장
                var mapRef: StorageReference = mStorageReference.child("maps/${myfile.name}")
                var uploadTask = mapRef.putFile(Uri.fromFile(myfile))
                uploadTask.addOnFailureListener {
                }.addOnSuccessListener {
                    print_log("succes" + storage.reference)

                    var newIntent= Intent(this,StopActivity::class.java)
                    startActivity(newIntent)
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
*/
            true

        }
    }
    fun onClick(view: View) {
        when(view.id){
            R.id.btn_pause -> pause()
            R.id.btn_stop -> stop()
            R.id.btn_restart -> restart()
        }
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

    fun pause(){
        var btn=findViewById<Button>(R.id.btn_pause)
        btn.visibility=View.GONE
        btn=findViewById<Button>(R.id.btn_stop)
        btn.visibility=View.VISIBLE
        btn=findViewById<Button>(R.id.btn_restart)
        btn.visibility=View.VISIBLE
        map.stop_Tracking()
        route_save = map.stop_Tracking()

    }

    fun stop() {    //타이머 멈추는거 만들어야함
        Toast.makeText(this,"종료를 원하시면, 길게 눌러주세요",Toast.LENGTH_LONG).show()


    }

    fun restart(){
        var btn=findViewById<Button>(R.id.btn_pause)
        btn.visibility=View.VISIBLE
        btn=findViewById<Button>(R.id.btn_stop)
        btn.visibility=View.GONE
        btn=findViewById<Button>(R.id.btn_restart)
        btn.visibility=View.GONE
        map.startTracking()
    }

    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }
}