package com.example.test_exchangerate.di

import com.example.test_exchangerate.retrofit.CurrencyRetrofitInterface

import com.google.android.datatransport.runtime.dagger.Provides
import com.google.gson.Gson
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private val client = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://openexchangerates.org/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideBlogService(retrofit: Retrofit.Builder): CurrencyRetrofitInterface {
        return retrofit
            .build()
            .create(CurrencyRetrofitInterface::class.java)
    }

}