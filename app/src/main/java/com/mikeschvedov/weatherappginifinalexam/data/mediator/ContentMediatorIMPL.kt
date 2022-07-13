package com.mikeschvedov.weatherappginifinalexam.data.mediator

import android.location.Address
import android.location.Geocoder
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import com.mikeschvedov.weatherappginifinalexam.data.database.repository.Repository
import com.mikeschvedov.weatherappginifinalexam.data.network.ApiManager
import com.mikeschvedov.weatherappginifinalexam.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


const val TAG = "Content Mediator"

class ContentMediatorIMPL @Inject constructor(
    private val apiManager: ApiManager,
    private val repository: Repository,
    private val geocoder: Geocoder
) : ContentMediator {

    override suspend fun updateDatabaseViaApi(lan: Double, lng: Double) {
        //Gini Office
        var lanDefault = 32.160
        var lngDefault = 34.810

        val location = getLocationViaCoordinates(lan, lng, false)
        println("updateDatabaseViaApi - location:${location} ,coordinate: ${lan} ${lng}")
        // Getting new data from the api
        getWeatherFromApi(lan, lng)
            .collect { response ->
                val weather = CurrentWeather(
                    summery = response.weather.summary,
                    tempCelsius = response.weather.temperature.toCelsius().toTwoDecimalAndString(),
                    forLocation = location,
                    icon = response.weather.icon
                )
                // Updating the database using the new data
                println("After getWeatherFromApi - weather:${weather.summery}")
                updateWeatherInDB(weather)
            }
    }

    override suspend fun getWeatherFromApi(lan: Double, lng: Double) = flow {
        val result = apiManager.getWeatherByLocation(lan, lng)
        if (result is Success) {
            val weatherData = result.data
            emit(weatherData)
        } else if (result is Failure) {
            Logger.e(TAG, result.exc?.message ?: "Unknown Error")
        }
    }

    override suspend fun getWeatherFromDatabase() = repository.getWeather()

    override suspend fun updateWeatherInDB(currentWeather: CurrentWeather) {
        println("--------------Updating data in database:${currentWeather.summery} | ${currentWeather.tempCelsius} | ${currentWeather.summery}")

        // Delete old data
        repository.deleteAllWeather()
        // Add new data
        repository.addWeather(currentWeather)
    }

    override suspend fun getLocationViaCoordinates(
        lan: Double,
        lng: Double,
        getOnlyCountry: Boolean
    ): String? {
        val addresses: List<Address>
        try {
            addresses = withContext(Dispatchers.Default) {
                geocoder.getFromLocation(
                    lan,
                    lng,
                    1
                )
            }
            if (addresses == null || addresses.isEmpty()) {
                return "Unknown"
            }

        } catch (e: Exception) {
            Logger.e(TAG, "${e.message}")
            return "Unknown"
        }
        // If we requested only a country name, or both country and city
        return if (getOnlyCountry) {
            addresses[0].countryName
        } else {
             "${addresses[0].countryName}, ${addresses[0].locality}"
        }
    }
}