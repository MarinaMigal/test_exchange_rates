package com.example.test_exchangerate


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_exchangerate.ui.adapters.CurrencyRecyclerAdapter
import com.example.test_exchangerate.ui.interfaces.CurrencyApiInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.test_exchangerate.db.CurrencyRatesDB
import com.example.test_exchangerate.db.CurrencyRoomDatabase
import com.example.test_exchangerate.ui.adapters.CurrencyDBRecyclerAdapter
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
                apiCall(currencyRecyclerView,currencyDb)
            }else {
                val lastTimestamp = currencyDb.currencyRatesDao().getLastTimestamp()
                Log.d("MainActivity", "onCreate: $lastTimestamp")
                val currentTS = Instant.now().epochSecond
                val diffTime: Long = (currentTS - lastTimestamp) / 60
                if (diffTime < 10) {
                    val ratesDB = currencyDb.currencyRatesDao().getCurrencyRates()
                    withContext(Dispatchers.Main) {
                        currencyRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = CurrencyDBRecyclerAdapter(ratesDB)
                        }
                    }
                    Log.d("MainActivity", "onCreate: $currentTS from db")
                }else apiCall(currencyRecyclerView,currencyDb)
            }

        }

        }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun apiCall(currencyRecyclerView:RecyclerView, currencyDb:CurrencyRoomDatabase){
        val request = ApiClient.buildService(CurrencyApiInterface::class.java)
        val response = request.getLatestRates(API_KEY, BASE)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                currencyRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = CurrencyRecyclerAdapter(response.body()!!)
                }
            }
            var currencyRate: CurrencyRatesDB
            val lastRequestTS = Instant.from(
                DateTimeFormatter.RFC_1123_DATE_TIME.parse(
                    response.headers().get("Date")
                )
            ).epochSecond
            currencyDb.currencyRatesDao().deleteAll()
            for ((key, value) in response.body()!!.rates) {
                if (key != "USD") {
                    currencyRate = CurrencyRatesDB(key, value, response.body()!!.timestamp, lastRequestTS)
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                    currencyDb.currencyRatesDao().insert(listOf(currencyRate))
                }
            }
            Log.d(
                "MainActivity",
                "onCreate: ${response.body()?.timestamp.toString()} from retrofit"
            )
            Log.d("MainActivity", "onCreate: $lastRequestTS  from retrofit")

        }
    }
}


