package com.example.test_exchangerate.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Api for the latest currency rates
private const val BASE_URL = "https://openexchangerates.org/api/"

//Api for the historical exchange rate for a given time period
private const val BASE_URL2 = "https://api.exchangerate.host/"

object ApiClient {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val retrofitChart = Retrofit.Builder()
        .baseUrl(BASE_URL2)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun <T> buildServiceChart(service: Class<T>): T {
        return retrofitChart.create(service)
    }
}

