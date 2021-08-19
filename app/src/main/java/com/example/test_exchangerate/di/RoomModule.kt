package com.example.test_exchangerate.di

import android.content.Context
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.test_exchangerate.room.CurrencyRatesDAO
import com.example.test_exchangerate.room.CurrencyRoomDatabase
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun provideBlogDb(@ApplicationContext context: Context): CurrencyRoomDatabase {
        return databaseBuilder(
                context,
            CurrencyRoomDatabase::class.java,
            CurrencyRoomDatabase.DATABASE_NAME)
            //.fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideBlogDAO(currencyRoomDatabase: CurrencyRoomDatabase): CurrencyRatesDAO {
        return currencyRoomDatabase.currencyRatesDao()
    }
}