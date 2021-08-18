package com.example.test_exchangerate.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test_exchangerate.ChartActivity
import com.example.test_exchangerate.R
import com.example.test_exchangerate.data.model.CurrencyRate
import com.example.test_exchangerate.db.CurrencyRatesDB

class CurrencyDBRecyclerAdapter(private val currencyRates: List<CurrencyRatesDB?>?) : RecyclerView.Adapter<CurrencyDBRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.currency_rate_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.layout.context
        if (currencyRates != null) {
                    holder.currency.text = currencyRates[position]!!.currency
                    holder.rate.text = String.format("%.2f", currencyRates[position]!!.rate)
            holder.layout.setOnClickListener {
                Log.d("ChartActivity", "onClick: clicked on: $currencyRates[position]!!.currency")
                val intent  = Intent(context, ChartActivity::class.java)
                intent.putExtra("currency",  holder.currency.text)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = currencyRates!!.size

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val currency = itemView.findViewById<TextView>(R.id.currency)!!
        val rate = itemView.findViewById<TextView>(R.id.rate)!!
        val layout  = itemView.findViewById<LinearLayout>(R.id.container1)!!
    }
}

