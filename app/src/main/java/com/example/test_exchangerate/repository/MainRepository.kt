package com.example.test_exchangerate.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.test_exchangerate.data.model.Rates
import com.example.test_exchangerate.retrofit.CurrencyRetrofitInterface
import com.example.test_exchangerate.retrofit.NetworkMapper
import com.example.test_exchangerate.room.CacheMapper
import com.example.test_exchangerate.room.CurrencyRatesCacheEntity
import com.example.test_exchangerate.room.CurrencyRatesDAO
import com.example.test_exchangerate.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val API_KEY = "9ec0925427134a09a76cdbb1a0662c87"
private const val BASE = "USD"
class MainRepository @Inject constructor(
    private val currencyRatesDao: CurrencyRatesDAO,
    private val retrofit: CurrencyRetrofitInterface,
    private val cacheMapper: CacheMapper,
    private val networkMapper: NetworkMapper
){
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRates(): Flow<DataState<Rates>> = flow {
        try{
            if(currencyRatesDao.getCurrencyRates().isNullOrEmpty()){
                emit(DataState.Success(cacheMapper.mapFromEntityList(cacheNetworkData())))
            }
            else {
                val lastTimestamp = currencyRatesDao.getLastTimestamp()
                val currentTS = Instant.now().epochSecond
                val diffTime: Long = (currentTS - lastTimestamp) / 60
                if (diffTime < 10) {
                    val cachedRates = currencyRatesDao.getCurrencyRates()
                    emit(DataState.Success(cacheMapper.mapFromEntityList(cachedRates)))
                } else  emit(DataState.Success(cacheMapper.mapFromEntityList(cacheNetworkData())))
            }
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cacheNetworkData(): List<CurrencyRatesCacheEntity?>? {
        val networkBlogs = retrofit.getLatestRates(API_KEY, BASE)
        var rates: CurrencyRatesCacheEntity
        val lastRequestTS = Instant.from(
            DateTimeFormatter.RFC_1123_DATE_TIME.parse(
                networkBlogs.headers().get("Date")
            )
        ).epochSecond
        currencyRatesDao.deleteAll()
        for ((key, value) in networkBlogs.body()!!.rates) {
            if (key != "USD") {
                rates = CurrencyRatesCacheEntity(
                    key,
                    value,
                    networkBlogs.body()!!.timestamp,
                    lastRequestTS
                )
                currencyRatesDao.insert(listOf(rates))
                currencyRatesDao.insert(listOf(rates))
            }
        }
        return currencyRatesDao.getCurrencyRates()

    }
}