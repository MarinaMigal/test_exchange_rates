package com.example.test_exchangerate.ui.interfaces

import com.example.test_exchangerate.data.model.CurrencyRate
import com.example.test_exchangerate.data.model.CurrencyRatesTimeSeries
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiInterface {
    @GET("latest.json")
    suspend fun getLatestRates(
        @Query("app_id") app_id: String,
            @Query("base") base : String
    ): Response<CurrencyRate>

    @GET("timeseries")
    suspend fun getCurrencyTimeSeries(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("base") base : String,
        @Query("symbols") symbols : String
    ): Response<CurrencyRatesTimeSeries>

}