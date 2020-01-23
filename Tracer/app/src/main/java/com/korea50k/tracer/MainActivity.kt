package com.korea50k.tracer

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.korea50k.tracer.dataClass.MapData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    val TAG = "ssmm11"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        val mapData = MapData("jung_beengle", "자기전_map", "겹쳐져 버리나",
            "스토리지에 있는 json 파일 경로", "스토리지에 있는 image 파일 경로", "10.32km","12:22",2,0,"RANKING")
        db.collection("mapData").document("firstt_map").set(mapData)

    }

}
