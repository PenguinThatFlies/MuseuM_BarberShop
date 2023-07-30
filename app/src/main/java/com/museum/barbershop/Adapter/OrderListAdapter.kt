package com.museum.barbershop.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.museum.barbershop.Modul.OrderData
import com.museum.barbershop.Modul.ReviewData
import com.museum.barbershop.R

class OrderListAdapter(private val context: Context,
                    private val products: ArrayList<OrderData>,
                    private val listener: (OrderData) -> Unit
) : RecyclerView.Adapter<OrderListAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fullnameorder: TextView = itemView.findViewById(R.id.fullnameorder)
        private val barberorder: TextView = itemView.findViewById(R.id.barberorder)
        private val daytoday: TextView = itemView.findViewById(R.id.daytoday)
        private val datatoday: TextView = itemView.findViewById(R.id.datatoday)

        @SuppressLint("SetTextI18n")
        fun bindView(product: OrderData, listener: (OrderData) -> Unit) {

            fullnameorder.text = product.User_Fullname
            barberorder.text = product.Barber_Fullname
            daytoday.text = product.Time
//            datatoday.text = "${product.Day} Day"


            when(product.Month){
                "0" -> datatoday.text = "${product.Day} Jan"
                "1" -> datatoday.text = "${product.Day} Feb"
                "2" -> datatoday.text = "${product.Day} Mar"
                "3" -> datatoday.text = "${product.Day} Apr"
                "4" -> datatoday.text = "${product.Day} May"
                "5" -> datatoday.text = "${product.Day} Jun"
                "6" -> datatoday.text = "${product.Day} Jul"
                "7" -> datatoday.text = "${product.Day} Aug"
                "8" -> datatoday.text = "${product.Day} Sep"
                "9" -> datatoday.text = "${product.Day} Oct"
                "10" -> datatoday.text = "${product.Day} Nov"
                "11" -> datatoday.text = "${product.Day} Dec"
            }


            itemView.setOnClickListener { listener(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_orders, parent, false)
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