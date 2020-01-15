package com.korea50k.RunShare.Activities.Join

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.item_age.view.*

class AgeAdapter(
    val age: ArrayList<String>
) :
    RecyclerView.Adapter<AgeAdapter.MainViewHolder>() {
    /* (1) Adapter의 파라미터에 람다식 itemClick을 넣는다. */

    var list: List<Int> = arrayListOf()
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_age, parent, false)
        return MainViewHolder(view)
        /* (4) Holder의 파라미터가 하나 더 추가됐으므로, 이곳에도 추가해준다. */
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        //holder.ageText!!.setText(age.get(position))
        tracker?.let {
            holder.bind(age.get(position), it.isSelected(position.toLong()))
        }

    }

    override fun getItemCount(): Int {
        return age.size
    }

    inner class MainViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {

        /* (2) Holder에서 클릭에 대한 처리를 할 것이므로, Holder의 파라미터에 람다식 itemClick을 넣는다. */
        val ageText = itemView?.ageTextView

        fun bind(age: String, isActivated: Boolean = false) {
            // 데이터와 뷰 연결
            ageText!!.text = age
            itemView.isActivated = isActivated

            /* (3) itemView가 클릭됐을 때 처리할 일을 itemClick으로 설정한다.
            (Dog) -> Unit 에 대한 함수는 나중에 MainActivity.kt 클래스에서 작성한다. */
        }
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }
}
