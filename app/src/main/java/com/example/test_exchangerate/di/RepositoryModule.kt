package com.example.test_exchangerate.di

import com.example.test_exchangerate.repository.MainRepository
import com.example.test_exchangerate.retrofit.CurrencyRetrofitInterface
import com.example.test_exchangerate.retrofit.NetworkMapper
import com.example.test_exchangerate.room.CacheMapper
import com.example.test_exchangerate.room.CurrencyRatesDAO

import com.google.android.datatransport.runtime.dagger.Provides
import dagger.Module
import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module

@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        currencyRatesDao: CurrencyRatesDAO,
        retrofit: CurrencyRetrofitInterface,
        cacheMapper: CacheMapper,
        networkMapper: NetworkMapper
    ): MainRepository {
        return MainRepository(currencyRatesDao, retrofit, cacheMapper, networkMapper)
    }
}