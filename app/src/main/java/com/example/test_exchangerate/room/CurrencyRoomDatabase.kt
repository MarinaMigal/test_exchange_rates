package com.example.test_exchangerate.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrencyRatesCacheEntity::class], version = 1, exportSchema = false)
abstract class CurrencyRoomDatabase : RoomDatabase() {

    abstract fun currencyRatesDao(): CurrencyRatesDAO

    companion object {

        @Volatile
        private var INSTANCE: CurrencyRoomDatabase? = null

        fun getInstance(context: Context): CurrencyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyRoomDatabase::class.java,
                    "currency_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}