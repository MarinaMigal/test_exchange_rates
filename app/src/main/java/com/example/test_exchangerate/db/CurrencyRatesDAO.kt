package com.example.test_exchangerate.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp


@Dao
interface CurrencyRatesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(rates: List<CurrencyRatesDB>)

    @Query("DELETE FROM currency_rates")
    suspend fun deleteAll()

    @Query("SELECT * FROM currency_rates ")
    suspend fun getCurrencyRates(): List<CurrencyRatesDB?>?

    @Query("SELECT lastRequestTS FROM currency_rates LIMIT 1")
    suspend fun getLastTimestamp():Long

}