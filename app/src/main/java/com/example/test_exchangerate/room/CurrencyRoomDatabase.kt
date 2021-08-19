package com.example.test_exchangerate.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyRatesCacheEntity::class], version = 1, exportSchema = false)
abstract class CurrencyRoomDatabase : RoomDatabase() {

    abstract fun currencyRatesDao(): CurrencyRatesDAO

    companion object {
        const val DATABASE_NAME: String = "currency_database"

    }
}