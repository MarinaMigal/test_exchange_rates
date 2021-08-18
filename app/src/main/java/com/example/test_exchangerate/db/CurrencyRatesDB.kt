package com.example.test_exchangerate.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.*

@Entity(tableName = "currency_rates")
data class CurrencyRatesDB(
    @PrimaryKey
    @ColumnInfo(name="currency")
    val currency:String,
    @ColumnInfo(name="rate")
    val rate:Double,
    @ColumnInfo(name="timestamp")
    val timestamp: Long,
   @ColumnInfo(name="lastRequestTS")
   val lastRequestTS: Long
)
