package com.superdroid.facemaker.FormClass

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.superdroid.facemaker.Activity.LoadActivity
import com.superdroid.facemaker.R
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapter(private val items: ArrayList<MapList>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {it ->
            Toast.makeText(it.context, "Clicked: ${item.map_name}", Toast.LENGTH_SHORT).show()
            var newIntent= Intent(it.context, LoadActivity::class.java)
            it.context.startActivity(newIntent)
            //GlobalBus.getBus()?.post(Events.LoadToMain(it.map_name.text.toString()))
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            private var view: View = v
            fun bind(listener: View.OnClickListener, item: MapList) {
                view.index.text = item.index.toString()
                view.map_name.text = item.map_name
                view.setOnClickListener(listener)
            }
        }
    }
