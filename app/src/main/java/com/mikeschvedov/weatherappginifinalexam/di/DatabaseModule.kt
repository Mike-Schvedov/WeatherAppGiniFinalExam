package com.mikeschvedov.weatherappginifinalexam.di

import android.content.Context
import androidx.room.Room
import com.mikeschvedov.weatherappginifinalexam.data.database.WeatherDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DBName = "WeatherDB"

    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(appContext, WeatherDB::class.java, DBName)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWeatherDao(weatherDB: WeatherDB) =
        weatherDB.weatherDao()
}