package com.umpa2020.tracer.main.challenge

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.extensions.format
import kotlinx.android.synthetic.main.fragment_challenge.*
import kotlinx.android.synthetic.main.fragment_challenge.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChallengeFragment : Fragment() {

  var dateFormat = SimpleDateFormat("dd MMM, YYY", Locale.KOREA)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment


    val view: View = inflater.inflate(R.layout.fragment_challenge, container, false)
    val challengeDatas = arrayListOf<ChallengeData>()
    val now = Calendar.getInstance()


    //TODO
    //btn_challenge_from.text = dateFormat.format(now.time)
    //btn_challenge_from.text = now.time.format(YEAR_MONTH_DAY)
    //btn_challenge_to.text = dateFormat.format(now.time)


    var challengeData = ChallengeData(R.drawable.button_background,"제주 그란폰도","2019. 04. 01","제주")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"철원 DMZ 랠리","2019. 07. 03","강원")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"양양 그란폰도","2019. 08. 21","경기")
    challengeDatas.add(challengeData)
    challengeData = ChallengeData(R.drawable.button_background,"화천 DMZ 랠리","2019. 06. 21","강원")
    challengeDatas.add(challengeData)

    view.btn_challenge_from.setOnClickListener {
     val datePicker = DatePickerDialog(
       requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
         val selectedDate = Calendar.getInstance()
         selectedDate.set(Calendar.YEAR, year)
         selectedDate.set(Calendar.MONTH, month)
         selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
         btn_challenge_from.text = selectedDate.time.time.format(Locale.getDefault())
       },
       now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
     )
      datePicker.show()
    }

    view.btn_challenge_to.setOnClickListener{
      val datePicker = DatePickerDialog(
        requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
          val selectedDate = Calendar.getInstance()
          selectedDate.set(Calendar.YEAR, year)
          selectedDate.set(Calendar.MONTH, month)
          selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
          btn_challenge_to.text = selectedDate.time.time.format(Locale.getDefault())
        },
        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
      )
      datePicker.show()
    }

    view.btn_challenge_region.setOnClickListener{
      val regionChoicePopup = RegionChoicePopup(requireContext())
      regionChoicePopup.show()
    }

    view.btn_challenge_search.setOnClickListener{

    }




    view.challenge_recycler_view.adapter = ChallengeRecyclerViewAdapter(challengeDatas)
    view.challenge_recycler_view.layoutManager = GridLayoutManager(context, 2)
    return view
  }
}
