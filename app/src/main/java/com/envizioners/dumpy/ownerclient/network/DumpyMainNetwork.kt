package com.envizioners.dumpy.ownerclient.network

import android.content.Context
import android.util.Log
import com.envizioners.dumpy.ownerclient.preference.UserPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.UnknownHostException

class DumpyMainNetwork {

    private val loggingIntercetor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    fun <Api> buildAPI(
        api: Class<Api>,
        context: Context
    ): Api {

        var authKey = ""
        var okHttpClient: OkHttpClient

        runBlocking {
            authKey = UserPreference.getUserAuthorization(context, "USER_API_KEY")
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        it.addHeader("x-api-key", authKey)
                    }.build())
                }
                .addInterceptor(loggingIntercetor)
                .build()
        }

        return Retrofit.Builder()
            .baseUrl("https://dumpyph.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(api)
    }
}