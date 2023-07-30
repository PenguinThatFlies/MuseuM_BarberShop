package com.museum.barbershop.Adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.museum.barbershop.Modul.TimeData
import com.museum.barbershop.R

class TimeAdapter(private val context: Context,
                   private val times: List<TimeData>,
                   private val listener: (TimeData) -> Unit
) : RecyclerView.Adapter<TimeAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.clock)

        fun bindView(time: TimeData, listener: (TimeData) -> Unit) {

            name.text = time.time

            itemView.setOnClickListener {
                listener(time)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_time, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val product = times[position]
        holder.bindView(product, listener)
    }

    override fun getItemCount(): Int {
        return times.size
    }
}
