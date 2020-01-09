package com.korea50k.RunShare.Join

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_age_select.*
import kotlinx.android.synthetic.main.activity_gender_select.app_toolbar
import kotlinx.android.synthetic.main.signup_toolbar.view.*

class AgeSelectActivity : AppCompatActivity() {
    val WSY = "WSY"

    var tracker : SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_select)
        val titleText = app_toolbar.titleText
        titleText.setText("나이를 선택하세요.")

        // recyclerView data 준비
        // 나이 16세~90세
        val age = ArrayList<String>()
        for(i in 16..90)
            age.add(i.toString())

        //ageRecycler.adapter = AgeAdapter(this, age)
        val adapter = AgeAdapter(age)

        ageRecycler.layoutManager = LinearLayoutManager(this)
        ageRecycler.adapter = adapter
        adapter.notifyDataSetChanged()

        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            ageRecycler,
            StableIdKeyProvider(ageRecycler),
            MyItemDetailsLookup(ageRecycler),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker
    }

    fun onClick(v : View){
        when(v.id){
            R.id.backImageBtn ->
            {
                finish()
            }
        }
    }
}
