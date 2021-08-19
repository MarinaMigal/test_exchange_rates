package com.example.test_exchangerate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyRatesNetworkEntity(
    @SerializedName("base")
    val base: String,
    @SerializedName("disclaimer")
    val disclaimer: String,
    @SerializedName("license")
    val license: String,
    @SerializedName("rates")
    val rates: LinkedHashMap<String, Double> = LinkedHashMap(),
    @SerializedName("timestamp")
    val timestamp: Long
)
