package com.superdroid.facemaker

import android.content.Intent
import com.superdroid.facemaker.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import java.io.File

class LoadActivity : AppCompatActivity() {
    lateinit var map_List: ArrayList<MapList>       //index와 파일의 이름을 담은 클래스의 ArrayList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_activity)

        loadFile()  //폴더에서 파일목록 불러오기
        val adapter = RecyclerAdapter(map_List)     //map_List를 이용해 adapter생성
        val recyclerView = findViewById<RecyclerView>(R.id.map_listview)
        recyclerView.adapter= adapter
    }
    private fun loadFile() {
        map_List= ArrayList<MapList>()
        val  map_Path=filesDir
        var folder = File(map_Path,"mapdata")
        val listFiles=folder.listFiles()
        var i=0
        for(map in listFiles!!){
            map_List.add(MapList(i++,map.name))
        }
    }

}