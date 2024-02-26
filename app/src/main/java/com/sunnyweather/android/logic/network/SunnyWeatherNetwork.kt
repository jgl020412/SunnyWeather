package com.sunnyweather.android.logic.network

import android.util.Log
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

object SunnyWeatherNetwork {

    private const val TAG = "SunnyWeatherNetwork"

    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun searchPlaces(query: String) =
        placeService.searchPlaces(query, SunnyWeatherApplication.TOKEN).await()


    suspend fun getDailyWeather(lng: String, lat: String): DailyResponse {
        Log.d(TAG, "getDailyWeather: $lng, $lat, ${SunnyWeatherApplication.TOKEN}")
        val temp = weatherService.getDailyWeather(SunnyWeatherApplication.TOKEN, lng, lat).await()
        Log.d(TAG, "getDailyWeather: ${temp}")
        return temp
    }

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(SunnyWeatherApplication.TOKEN, lng, lat).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}



