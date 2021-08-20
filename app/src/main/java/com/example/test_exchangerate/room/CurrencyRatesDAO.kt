package com.example.test_exchangerate.room

import androidx.room.*


@Dao
interface CurrencyRatesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rates: List<CurrencyRatesCacheEntity>)

    @Query("DELETE FROM currency_rates")
    suspend fun deleteAll()

    @Query("SELECT * FROM currency_rates ")
    suspend fun getCurrencyRates(): List<CurrencyRatesCacheEntity?>?

    @Query("SELECT lastRequestTS FROM currency_rates LIMIT 1")
    suspend fun getLastTimestamp(): Long

}