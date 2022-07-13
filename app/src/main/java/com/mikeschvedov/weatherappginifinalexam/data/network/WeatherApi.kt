package com.mikeschvedov.weatherappginifinalexam.data.network

import com.mikeschvedov.weatherappginifinalexam.models.response.WeatherWrapper
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {

    @GET("weather/latest/by-lat-lng")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): WeatherWrapper

}
