package com.mikeschvedov.weatherappginifinalexam.data.database.repository

import androidx.lifecycle.LiveData
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun addWeather(weather: CurrentWeather)
    suspend fun getWeather(): Flow<CurrentWeather>
    suspend fun deleteAllWeather()
}