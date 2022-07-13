package com.mikeschvedov.weatherappginifinalexam.data.mediator

import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import com.mikeschvedov.weatherappginifinalexam.models.response.WeatherWrapper
import kotlinx.coroutines.flow.Flow

interface ContentMediator {
    suspend fun getWeatherFromApi(lan: Double, lng: Double) : Flow<WeatherWrapper>

    suspend fun updateWeatherInDB(currentWeather: CurrentWeather)

    suspend fun getLocationViaCoordinates(lan: Double, lng: Double, getOnlyCountry: Boolean) : String?

    suspend fun getWeatherFromDatabase() : Flow<CurrentWeather>

    suspend fun updateDatabaseViaApi(lan: Double, lng: Double)
}