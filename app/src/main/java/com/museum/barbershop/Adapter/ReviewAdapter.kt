package com.museum.barbershop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.museum.barbershop.Modul.ReviewData
import com.museum.barbershop.R

class ReviewAdapter(private val context: Context,
                   private val products: ArrayList<ReviewData>,
                   private val listener: (ReviewData) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.namelast)
        private val reviewuser: TextView = itemView.findViewById(R.id.reviewuser)
        fun bindView(product: ReviewData, listener: (ReviewData) -> Unit) {

            name.text = product.username
            reviewuser.text = product.review

            itemView.setOnClickListener { listener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_review, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val product = products[position]
        holder.bindView(product, listener)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}