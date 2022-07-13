package com.mikeschvedov.weatherappginifinalexam.di

import android.content.Context
import android.location.Geocoder
import com.mikeschvedov.weatherappginifinalexam.data.database.WeatherDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*

@Module
@InstallIn(SingletonComponent::class)
object GeneralModule {

    @Provides
    fun provideGeocoder(@ApplicationContext appContext: Context) =
        Geocoder(appContext, Locale.getDefault())

}