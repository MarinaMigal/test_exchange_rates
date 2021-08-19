package com.example.test_exchangerate.data.model

data class Rates(
    val rates : LinkedHashMap<String, Double>,
    val timestamp: Long,
    val lastRequestTS: Long
)
