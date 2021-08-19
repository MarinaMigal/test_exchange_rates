package com.example.test_exchangerate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyRatesTimeSeries(
    @SerializedName("base")
    val base: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("motd")
    val motd: HashMap<String,String> = HashMap(),
   @SerializedName("rates")
    val rates: LinkedHashMap<String,Map<String,Double>> = LinkedHashMap(),
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("timeseries")
    val timeseries: Boolean
)
