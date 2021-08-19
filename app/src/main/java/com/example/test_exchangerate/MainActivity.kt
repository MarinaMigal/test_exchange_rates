package com.example.test_exchangerate


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.ui.adapters.CurrencyRecyclerAdapter

import com.example.test_exchangerate.room.CurrencyRoomDatabase
import com.example.test_exchangerate.ui.rest.MainStateEvent
import com.example.test_exchangerate.ui.rest.MainViewModel
import com.example.test_exchangerate.util.DataState

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.time.Instant
import java.time.format.DateTimeFormatter


@ExperimentalCoroutinesApi
@AndroidEntryPoint

class MainActivity: AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subscribeObservers()
        viewModel.setStateEvent(MainStateEvent.GetRatesEvent)

        }
    private fun subscribeObservers(){
        viewModel.dataState.observe(this, Observer { dataState ->
            when(dataState){
                is DataState.Success<Rates> -> {
                    appendRates(dataState.data)
                }
                is DataState.Error -> {

                    displayError(dataState.exception.message)
                }
                DataState.Loading -> TODO()
            }
        })
    }

    private fun displayError(message: String?){
        if(message != null) Toast.makeText(this, message, Toast.LENGTH_LONG) else  Toast.makeText(this, "Unknown error.", Toast.LENGTH_LONG)
    }

    private fun appendRates(rates: Rates){
        val currencyRecyclerView: RecyclerView = findViewById(R.id.recycler_view)
        currencyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = CurrencyRecyclerAdapter(rates)
        }
    }




}


