package com.museum.barbershop.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.museum.barbershop.Modul.AuthModul.SignUpData
import com.museum.barbershop.R
import com.museum.barbershop.Review

class UserReviewAdapter(private val context: Context,
                   private val products: ArrayList<SignUpData>,
                   private val listener: (SignUpData) -> Unit
) : RecyclerView.Adapter<UserReviewAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.namelastuser)
        private val message: TextView = itemView.findViewById(R.id.reviewmes)

        private val star1: ImageView = itemView.findViewById(R.id.star1)
        private val star2: ImageView = itemView.findViewById(R.id.star2)
        private val star3: ImageView = itemView.findViewById(R.id.star3)
        private val star4: ImageView = itemView.findViewById(R.id.star4)
        private val star5: ImageView = itemView.findViewById(R.id.star5)

        fun bindView(product: SignUpData, listener: (SignUpData) -> Unit) {

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

//            for (i in product.star.toString()){
//                stringC += i
//                count += 1
//            }

//            count += product.star!!

            message.text = product.message

            itemView.setOnClickListener { listener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_review_user, parent, false)
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