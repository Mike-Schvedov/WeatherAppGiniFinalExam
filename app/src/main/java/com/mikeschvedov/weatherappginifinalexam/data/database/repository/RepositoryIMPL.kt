package com.mikeschvedov.weatherappginifinalexam.data.database.repository

import com.mikeschvedov.weatherappginifinalexam.data.database.daos.WeatherDao
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryIMPL @Inject constructor(
    private val weatherDao: WeatherDao,
) : Repository {
    override suspend fun addWeather(weather: CurrentWeather) = weatherDao.addCurrentWeather(weather)

    override suspend fun getWeather(): Flow<CurrentWeather> = weatherDao.getCurrentWeather()

    override suspend fun deleteAllWeather()  = weatherDao.deleteAll()
}