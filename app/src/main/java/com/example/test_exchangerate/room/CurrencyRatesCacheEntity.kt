package com.example.test_exchangerate.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRatesCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "currency")
    val currency: String,
    @ColumnInfo(name = "rate")
    val rate: Double,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "lastRequestTS")
    val lastRequestTS: Long
)
