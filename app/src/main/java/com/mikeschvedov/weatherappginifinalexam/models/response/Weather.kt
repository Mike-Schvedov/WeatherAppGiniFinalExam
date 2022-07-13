package com.mikeschvedov.weatherappginifinalexam.models.response

data class Weather(
    val apparentTemperature: Double,
    val cloudCover: Double,
    val dewPoint: Double,
    val humidity: Double,
    val icon: String,
    val lat: String,
    val lng: String,
    val ozone: Double,
    val precipIntensity: Double,
    val precipType: String,
    val pressure: Double,
    val summary: String,
    val temperature: Double,
    val time: Double,
    val visibility: Double,
    val windBearing: Double,
    val windGust: Double,
    val windSpeed: Double
)