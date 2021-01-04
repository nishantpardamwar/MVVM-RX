package com.nishantpardamwar.unnamed

import android.content.Context
import com.nishantpardamwar.unnamed.network.WikipediaServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private lateinit var context: Context
    lateinit var services: WikipediaServices
    fun init(context: Context) {
        this.context = context

        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        services = Retrofit.Builder().baseUrl(WikipediaServices.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuilder.build())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(WikipediaServices::class.java)
    }
}