package com.museum.barbershop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.R

class AdminAdapter(private val context: Context,
                   private val products: ArrayList<SignUpData>,
                   private val listener: (SignUpData) -> Unit
) : RecyclerView.Adapter<AdminAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSrc: ImageView = itemView.findViewById(R.id.image)
        private val name: TextView = itemView.findViewById(R.id.namelast)

        private val star1: ImageView = itemView.findViewById(R.id.star11)
        private val star2: ImageView = itemView.findViewById(R.id.star22)
        private val star3: ImageView = itemView.findViewById(R.id.star33)
        private val star4: ImageView = itemView.findViewById(R.id.star44)
        private val star5: ImageView = itemView.findViewById(R.id.star55)

        fun bindView(product: SignUpData, listener: (SignUpData) -> Unit) {
            Glide.with(itemView)
                .load(product.imageSrc)
                .into(imageSrc)
            name.text = product.username

            star1.visibility = View.GONE
            star2.visibility = View.GONE
            star3.visibility = View.GONE
            star4.visibility = View.GONE
            star5.visibility = View.GONE

            fun star0() {
                star1.setImageResource(R.drawable.baseline_star_border_24)
                star2.setImageResource(R.drawable.baseline_star_border_24)
                star3.setImageResource(R.drawable.baseline_star_border_24)
                star4.setImageResource(R.drawable.baseline_star_border_24)
                star5.setImageResource(R.drawable.baseline_star_border_24)
            }

            fun star1() {
                star1.visibility = View.VISIBLE
                star2.visibility = View.VISIBLE
                star3.visibility = View.VISIBLE
                star4.visibility = View.VISIBLE
                star5.visibility = View.VISIBLE
                star2.setImageResource(R.drawable.baseline_star_border_24)
                star3.setImageResource(R.drawable.baseline_star_border_24)
                star4.setImageResource(R.drawable.baseline_star_border_24)
                star5.setImageResource(R.drawable.baseline_star_border_24)
            }

            fun star2() {
                star1.visibility = View.VISIBLE
                star2.visibility = View.VISIBLE
                star3.visibility = View.VISIBLE
                star4.visibility = View.VISIBLE
                star5.visibility = View.VISIBLE
                star3.setImageResource(R.drawable.baseline_star_border_24)
                star4.setImageResource(R.drawable.baseline_star_border_24)
                star5.setImageResource(R.drawable.baseline_star_border_24)
            }

            fun star3() {
                star1.visibility = View.VISIBLE
                star2.visibility = View.VISIBLE
                star3.visibility = View.VISIBLE
                star4.visibility = View.VISIBLE
                star5.visibility = View.VISIBLE
                star4.setImageResource(R.drawable.baseline_star_border_24)
                star5.setImageResource(R.drawable.baseline_star_border_24)
            }

            fun star4() {
                star1.visibility = View.VISIBLE
                star2.visibility = View.VISIBLE
                star3.visibility = View.VISIBLE
                star4.visibility = View.VISIBLE
                star5.visibility = View.VISIBLE
                star5.setImageResource(R.drawable.baseline_star_border_24)
            }

            fun star5() {
                star1.visibility = View.VISIBLE
                star2.visibility = View.VISIBLE
                star3.visibility = View.VISIBLE
                star4.visibility = View.VISIBLE
                star5.visibility = View.VISIBLE
            }



            when(product.star){
                null -> star0()
                0 -> star0()
                1 -> star1()
                2 -> star2()
                3 -> star3()
                4 -> star4()
                5 -> star5()
                else -> star0()
            }

            itemView.setOnClickListener { listener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_barber, parent, false)
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
