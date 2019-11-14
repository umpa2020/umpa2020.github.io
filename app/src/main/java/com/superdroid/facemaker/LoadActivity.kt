package com.superdroid.facemaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class LoadActivity : AppCompatActivity() {
 //   lateinit var mStorageRef: StorageReference
    lateinit var map_List: ArrayList<MapList>       //index와 파일의 이름을 담은 클래스의 ArrayList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_fragment)

        loadFile()  //폴더에서 파일목록 불러오기
        val adapter = RecyclerAdapter(map_List)     //map_List를 이용해 adapter생성
        val recyclerView = findViewById<RecyclerView>(R.id.map_listview)
        recyclerView.adapter= adapter
    }
    private fun loadFile() {
        map_List= ArrayList<MapList>()
        var filelist=ArrayList<String>()
        if(intent.hasExtra("FILELIST")) {                    //로드가 된 경우
            filelist = intent.getStringArrayListExtra("FILELIST")   //intent에서 불러올 파일이름 가져오기
        }
        var i=0
        for(name in filelist!!){
            map_List.add(MapList(i++,name))
        }
    }

}