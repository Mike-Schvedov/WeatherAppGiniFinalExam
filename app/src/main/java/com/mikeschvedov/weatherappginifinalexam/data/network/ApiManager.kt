package com.mikeschvedov.weatherappginifinalexam.data.network

import com.mikeschvedov.weatherappginifinalexam.models.response.WeatherWrapper
import com.mikeschvedov.weatherappginifinalexam.util.ResultWrapper

interface ApiManager {
    suspend fun getWeatherByLocation(lat: Double, lng: Double): ResultWrapper<WeatherWrapper>
}