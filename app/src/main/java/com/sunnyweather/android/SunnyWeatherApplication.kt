package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.util.Properties

class SunnyWeatherApplication : Application() {
    companion object {
        lateinit var TOKEN: String
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        val resources = context.resources
        val inputStream = resources.openRawResource(R.raw.config)
        val properties = Properties()
        properties.load(inputStream)
        TOKEN = properties.getProperty("weather.token")
    }
}