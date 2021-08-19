package com.example.test_exchangerate.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRetrofitInterface {
    @GET("latest.json")
    suspend fun getLatestRates(
        @Query("app_id") app_id: String,
        @Query("base") base : String
    ): Response<CurrencyRatesNetworkEntity>
}