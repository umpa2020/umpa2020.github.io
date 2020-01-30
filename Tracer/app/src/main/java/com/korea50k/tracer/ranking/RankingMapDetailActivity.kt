package com.korea50k.tracer.ranking

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.korea50k.tracer.R
import kotlinx.android.synthetic.main.activity_rank_recycler_item_click.*
import kotlinx.android.synthetic.main.activity_ranking_map_detail.*
import kotlinx.android.synthetic.main.ranking_map_detail_popup.*

class RankingMapDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_map_detail)

        val intent = getIntent()
        //전달 받은 값으로 Title 설정
        var mapTitle = intent.extras?.getString("MapTitle").toString()
        rankingDetailMapTitle.text = mapTitle

        //버튼 누르면 연습용, 랭킹 기록용 선택 팝업 띄우기
        rankingDetailRaceButton.setOnClickListener{
            showPopup()
        }
    }

    /**
    * 팝업 띄우는 함수
     * */
    private fun showPopup(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.ranking_map_detail_popup, null)
        val textView : TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView)
        textView.text = "어떤 유형으로 경기하시겠습니까?"

        val textView1 : TextView = view.findViewById(R.id.rankingMapDetailPopUpTextView1)
        textView1.text = "연습용 : 루트 연습용(랭킹 등록 불가능)\n랭킹 등록용 : 랭킹 등록 가능"

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("유형을 선택해주세요.")
            .create()

        //연습용 버튼 눌렀을 때
        val practiceButton = view.findViewById<Button>(R.id.rankingMapDetailPracticeButton)
        practiceButton.setOnClickListener{
            Toast.makeText(this, "PracticeButton 클릭", Toast.LENGTH_SHORT).show()
        }


        //랭킹 기록용 버튼 눌렀을 때
        val recordButton = view.findViewById<Button>(R.id.rankingMapDetailRecordButton)
        recordButton.setOnClickListener{
            Toast.makeText(this, "RecordButton 클릭", Toast.LENGTH_SHORT).show()
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기

    }
}
