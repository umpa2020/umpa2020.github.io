package com.superdroid.facemaker.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.otto.Subscribe
import com.superdroid.facemaker.*
import com.superdroid.facemaker.EventBus.Events
import com.superdroid.facemaker.EventBus.GlobalBus
import com.superdroid.facemaker.FormClass.MapList
import com.superdroid.facemaker.FormClass.RecyclerAdapter

/**
 * A simple [Fragment] subclass.
 */
class LoadFragment() : Fragment() {
    lateinit var map_List: ArrayList<MapList>       //index와 파일의 이름을 담은 클래스의 ArrayList
    lateinit var filelist:ArrayList<String>
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var storage: FirebaseStorage
    lateinit var mStorageReference: StorageReference
    lateinit var myView:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()      //firebase 가져오기
        mStorageReference = storage.reference
        GlobalBus.getBus()?.register(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        GlobalBus.getBus()?.unregister(this)
    }
    @Subscribe
    fun getFileListBack(e: Events.FileListBack){
        filelist=e.filelist
        loadFile()
    }
    @Subscribe
    fun getImgBack(e:Events.imgBack){
        e.img
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.load_fragment, container, false)
        GlobalBus.getBus()
            ?.post(Events.FileListRequest())
        GlobalBus.getBus()?.post(Events.ImgRequest("test_route21.txt"))
        return myView
    }

    private fun loadFile() {
        map_List= ArrayList<MapList>()
        var i=0

        print_log("Succes to get Filelist")
        for(name in filelist){
            map_List.add(MapList(i++, name))
        }
        val adapter =
            RecyclerAdapter(map_List)     //map_List를 이용해 adapter생성
        val recyclerView =myView.findViewById<RecyclerView>(R.id.map_listview)
        recyclerView.adapter= adapter
    }
    fun print_log(text:String){
        Log.d(TAG,text.toString())
    }
}
