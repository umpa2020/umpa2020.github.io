package com.korea50k.RunShare.Activities.Profile


import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.Activities.RankFragment.RecyclerItemClickListener
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.dataClass.RankMapData
import com.korea50k.RunShare.dataClass.UserMapImageData
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList


class FragmentUserRace : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_race, container, false)
    }


}



