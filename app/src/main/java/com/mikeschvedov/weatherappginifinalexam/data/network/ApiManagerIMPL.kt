package com.mikeschvedov.weatherappginifinalexam.data.network

import com.mikeschvedov.weatherappginifinalexam.models.response.WeatherWrapper
import com.mikeschvedov.weatherappginifinalexam.util.Failure
import com.mikeschvedov.weatherappginifinalexam.util.ResultWrapper
import com.mikeschvedov.weatherappginifinalexam.util.Success
import javax.inject.Inject

class ApiManagerIMPL @Inject constructor(
    private val weatherApi: WeatherApi,
) : ApiManager {
    override suspend fun getWeatherByLocation(lat: Double, lng: Double): ResultWrapper<WeatherWrapper> = try {
        Success(weatherApi.getWeatherByLocation(lat, lng))
    } catch (e: Exception) {
        Failure(e)
    }
}