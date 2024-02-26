package com.sunnyweather.android.logic.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

//    private const val TAG = "ServiceCreator"

    private const val BASE_URL = "https://api.caiyunapp.com/"

//    val httpClient = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

//    class LoggingInterceptor: Interceptor {
//        override fun intercept(chain: Interceptor.Chain): Response {
//            val request = chain.request()
//            Log.d(TAG, "intercept: ${request.url()}")
//            val proceed = chain.proceed(request)
//            return proceed
//        }
//    }

}