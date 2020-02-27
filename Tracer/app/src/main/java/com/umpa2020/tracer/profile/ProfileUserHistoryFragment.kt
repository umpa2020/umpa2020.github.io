package com.umpa2020.tracer.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.HistoryData
import kotlinx.android.synthetic.main.fragment_profile_user_history.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileUserHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_profile_user_history, container, false)

        //TODO. 서버에서 데이터 받아옴
        val datas = ArrayList<HistoryData>()
        datas.add(HistoryData("test1", "50"))
        datas.add(HistoryData("test1", "150"))
        datas.add(HistoryData("test1", "250"))
        datas.add(HistoryData("test1", "350"))
        datas.add(HistoryData("test1", "450"))
        datas.add(HistoryData("test1", "550"))


        view.profileRecyclerHistory.adapter = ProfileRecyclerViewAdapterHistory(datas)
        view.profileRecyclerHistory.layoutManager = LinearLayoutManager(context!!)


        return view
    }


}
