package com.mikeschvedov.weatherappginifinalexam.models.response

import com.google.gson.annotations.SerializedName

data class WeatherWrapper(
    @SerializedName("data")
    val weather: Weather,
    val message: String
)