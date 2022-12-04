package com.envizioners.dumpy.ownerclient.network

import com.envizioners.dumpy.ownerclient.model_interfaces.DumpyOwnerService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationNetwork {

    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = System.getenv("SERVER_KEY")
    const val CONTENT_TYPE = "application/json"

    private val loggingIntercetor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingIntercetor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val notifAPI by lazy {
        retrofit.create(DumpyOwnerService::class.java)
    }
}