package com.mikeschvedov.weatherappginifinalexam.util

import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import com.mikeschvedov.weatherappginifinalexam.models.response.Weather

class NullableWrapper(val currentWeather: CurrentWeather? = null)