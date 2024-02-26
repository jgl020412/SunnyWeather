package com.sunnyweather.android.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    private lateinit var binging: ActivityWeatherBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        binging = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binging.root)
        if (viewModel.locationLng.isEmpty()) viewModel.locationLng =
            intent.getStringExtra("location_lng") ?: ""
        if (viewModel.locationLat.isEmpty()) viewModel.locationLat =
            intent.getStringExtra("location_lat") ?: ""
        if (viewModel.placeName.isEmpty()) viewModel.placeName =
            intent.getStringExtra("place_name") ?: ""
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气情况", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binging.swipeRefresh.isRefreshing = false
        })
        binging.swipeRefresh.setColorSchemeColors(R.color.colorPrimary)
        refreshWeather()
        binging.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        binging.now.navBtn.setOnClickListener {
            binging.drawerLayout.openDrawer(GravityCompat.START)
        }
        binging.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binging.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        binging.now.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        val currentTempText = "${realtime.temperature.toInt()}\u2103"
        binging.now.currentTemp.text = currentTempText
        binging.now.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        binging.now.currentAQI.text = currentPM25Text
        binging.now.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        binging.forecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this)
                .inflate(R.layout.forecast_item, binging.forecast.forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}\u2103"
            temperatureInfo.text = tempText
            binging.forecast.forecastLayout.addView(view)
        }

        val lifeIndex = daily.lifeIndex
        binging.lifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binging.lifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binging.lifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binging.lifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        binging.weatherLayout.visibility = View.VISIBLE
    }

}