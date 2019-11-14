package com.superdroid.facemaker


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.otto.Subscribe

/**
 * A simple [Fragment] subclass.
 */
class LoadFragment() : Fragment() {
    lateinit var map_List: ArrayList<MapList>       //index와 파일의 이름을 담은 클래스의 ArrayList
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var filelist:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalBus.getBus()?.register(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        GlobalBus.getBus()?.unregister(this)
    }
    @Subscribe
    fun connectEvent1(e:Events.Event1){
        print_log("Load : connectEvent1 :"+e.filelist.toString())
        filelist=e.filelist
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.load_fragment, container, false)

        loadFile()  //폴더에서 파일목록 불러오기
        val adapter = RecyclerAdapter(map_List)     //map_List를 이용해 adapter생성
        val recyclerView = view.findViewById<RecyclerView>(R.id.map_listview)
        recyclerView.adapter= adapter
        return view
    }

    private fun loadFile() {
        map_List= ArrayList<MapList>()
        var i=0
       for(name in filelist){
            map_List.add(MapList(i++,name))
        }
    }
    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }


}
