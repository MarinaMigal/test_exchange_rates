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
            //If the database is empty - get data from api and cache it
            if (currencyDb.currencyRatesDao().getCurrencyRates().isNullOrEmpty()) {
                cacheNetworkData(currencyRecyclerView, currencyDb)
                Log.d("MainActivity", "onCreate: retrofit")
            } else {
                //else get the timestamp of the last network request
                val lastTimestamp = currencyDb.currencyRatesDao().getLastTimestamp()
                //get the current timestamp
                val currentTS = Instant.now().epochSecond
                val diffTime: Long = (currentTS - lastTimestamp) / 60
                //if 10 min have not elapsed since the last request - get cached data from the database
                if (diffTime < 10) {
                    val cachedRates = currencyDb.currencyRatesDao().getCurrencyRates()
                    val cacheMapper: CacheMapper = CacheMapper()
                    val rates = cacheMapper.mapFromEntityList(cachedRates)
                    //display cached data
                    withContext(Dispatchers.Main) {
                        currencyRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = CurrencyRecyclerAdapter(rates)
                        }
                    }
                    Log.d("MainActivity", "onCreate: room")
                } else {
                    //else get data from api and cache it
                    cacheNetworkData(currencyRecyclerView, currencyDb)
                    Log.d("MainActivity", "onCreate: retrofit")
                }
            }

        }

    }

    //get data from api, display it and cache it
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cacheNetworkData(
        currencyRecyclerView: RecyclerView,
        currencyDb: CurrencyRoomDatabase
    ) {
        //get data from api
        val retrofit = ApiClient.buildService(CurrencyApiInterface::class.java)
        val networkBlogs = retrofit.getLatestRates(API_KEY, BASE)
        if (networkBlogs.isSuccessful) {
            val lastRequestTS = Instant.from(
                DateTimeFormatter.RFC_1123_DATE_TIME.parse(
                    networkBlogs.headers().get("Date")
                )
            ).epochSecond
            //display currency rates
            withContext(Dispatchers.Main) {
                currencyRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = CurrencyRecyclerAdapter(
                        Rates(
                            networkBlogs.body()!!.rates,
                            networkBlogs.body()!!.timestamp,
                            lastRequestTS
                        )
                    )
                }
            }
            //cache data to local database
            currencyDb.currencyRatesDao().deleteAll()
            for ((key, value) in networkBlogs.body()!!.rates) {
                if (key != "USD") {
                    var currencyRate = CurrencyRatesCacheEntity(
                        key,
                        value,
                        networkBlogs.body()!!.timestamp,
                        lastRequestTS
                    )
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                }
            }
        }
    }
}





