package com.example.test_exchangerate.retrofit



data class CurrencyRate(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: LinkedHashMap<String, Double> = LinkedHashMap(),
    val timestamp: Long
)