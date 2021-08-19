package com.example.test_exchangerate.data.model

import java.security.Timestamp

data class Rates(
    val rates : LinkedHashMap<String, Double>,
    val timestamp: Long,
    val lastRequestTS: Long
)
