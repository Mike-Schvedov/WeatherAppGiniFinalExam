package com.mikeschvedov.weatherappginifinalexam.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mikeschvedov.weatherappginifinalexam.data.database.daos.WeatherDao
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather


@Database(entities = [CurrentWeather::class], version = 1)
abstract class WeatherDB: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}