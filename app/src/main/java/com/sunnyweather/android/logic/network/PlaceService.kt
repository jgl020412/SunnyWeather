package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?lang=zh_CN")
    fun searchPlaces(
        @Query("query") query: String,
        @Query("token") token: String
    ): Call<PlaceResponse>
}