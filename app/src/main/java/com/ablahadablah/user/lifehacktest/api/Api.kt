package com.ablahadablah.user.lifehacktest.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class Api(baseUrl: String) {
    private val logging = HttpLoggingInterceptor(
        HttpLoggingInterceptor.Logger { message -> Log.d("MainLog-OkHttp", message) })
        .setLevel(HttpLoggingInterceptor.Level.BODY)
    
    private val okHttpClient = OkHttpClient.Builder()
        .apply {
            addInterceptor(logging)
        }.build()
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val company: CompanyApiService = retrofit.create(CompanyApiService::class.java)
}
