package com.mikeschvedov.weatherappginifinalexam.ui.maps

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediator
import com.mikeschvedov.weatherappginifinalexam.data.mediator.TAG
import com.mikeschvedov.weatherappginifinalexam.models.state.WeatherState
import com.mikeschvedov.weatherappginifinalexam.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val contentMediator: ContentMediator,
    val networkStatusChecker: NetworkStatusChecker
    ) : ViewModel() {

    private var _weatherState = MutableStateFlow(WeatherState("", "", ""))
    val weatherState = _weatherState.asStateFlow()

    private val _locationName = MutableStateFlow("")
    val locationName = _locationName.asStateFlow()

    // When all data is ready
    val weatherReady =
        combine(_weatherState, _locationName) { weatherState, locationName ->
            val isWeatherReady =
                weatherState.summary.isNotEmpty() && weatherState.icon.isNotEmpty() && weatherState.temp.isNotEmpty()
            val isLocationNameReady = locationName.isNotEmpty()
            return@combine isWeatherReady and isLocationNameReady
        }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)


    fun displayLocationInfo(lan: Double, lng: Double) {

        viewModelScope.launch {
            // Getting weather data for the clicked location
            contentMediator.getWeatherFromApi(lan, lng)
                .flowOn(Dispatchers.IO)
                .map {
                    WeatherState(
                        summary = it.weather.summary,
                        temp = it.weather.temperature.toCelsius().toTwoDecimalAndString(),
                        icon = it.weather.icon
                    )
                }
                .collect { weather ->
                    _weatherState.value = weather
                }

            // Get location name using geocoder
            _locationName.value =
                contentMediator.getLocationViaCoordinates(lan, lng, true).toString()
        }
    }
}