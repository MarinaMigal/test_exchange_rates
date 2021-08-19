package com.example.test_exchangerate


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.retrofit.ApiClient
import com.example.test_exchangerate.ui.adapters.CurrencyRecyclerAdapter
import com.example.test_exchangerate.retrofit.CurrencyApiInterface
import com.example.test_exchangerate.room.CacheMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.test_exchangerate.room.CurrencyRatesCacheEntity
import com.example.test_exchangerate.room.CurrencyRoomDatabase
import java.time.Instant
import java.time.format.DateTimeFormatter


private const val API_KEY = "9ec0925427134a09a76cdbb1a0662c87"
private const val BASE = "USD"

class MainActivity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currencyRecyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val context: Context = applicationContext
        val currencyDb = CurrencyRoomDatabase.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            if(currencyDb.currencyRatesDao().getCurrencyRates().isNullOrEmpty() )
            {
                cacheNetworkData(currencyRecyclerView,currencyDb)
            }else {
                val lastTimestamp = currencyDb.currencyRatesDao().getLastTimestamp()
                val currentTS = Instant.now().epochSecond
                val diffTime: Long = (currentTS - lastTimestamp) / 60
                Log.d("MainActivity", "onCreate: $lastTimestamp")
                if (diffTime < 10) {
                    val cachedRates = currencyDb.currencyRatesDao().getCurrencyRates()
                    val cacheMapper : CacheMapper = CacheMapper()
                    val rates = cacheMapper.mapFromEntityList(cachedRates)
                    withContext(Dispatchers.Main) {
                        currencyRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = CurrencyRecyclerAdapter(rates)
                        }
                    }
                    Log.d("MainActivity", "onCreate: $currentTS from room")
                }else cacheNetworkData(currencyRecyclerView,currencyDb)
            }

        }

     }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cacheNetworkData(currencyRecyclerView:RecyclerView, currencyDb:CurrencyRoomDatabase) {
        val retrofit = ApiClient.buildService(CurrencyApiInterface::class.java)
        val networkBlogs = retrofit.getLatestRates(API_KEY, BASE)
        if (networkBlogs.isSuccessful) {
            val lastRequestTS = Instant.from(
                DateTimeFormatter.RFC_1123_DATE_TIME.parse(
                    networkBlogs.headers().get("Date")
                )
            ).epochSecond
            currencyDb.currencyRatesDao().deleteAll()
            for ((key, value) in networkBlogs.body()!!.rates) {
                if (key != "USD") {
                    var currencyRate = CurrencyRatesCacheEntity(key, value, networkBlogs.body()!!.timestamp, lastRequestTS)
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                }
            }
            val cacheMapper : CacheMapper = CacheMapper()
            val rates : Rates = cacheMapper.mapFromEntityList(currencyDb.currencyRatesDao().getCurrencyRates())
            withContext(Dispatchers.Main) {
                currencyRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = CurrencyRecyclerAdapter(rates)
                }
            }
        }
        }
    }





