package com.mikeschvedov.weatherappginifinalexam.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeschvedov.weatherappginifinalexam.data.database.entities.CurrentWeather
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrentWeather(currentWeather: CurrentWeather)

    @Query("SELECT * FROM currentweather")
    fun getCurrentWeather() : Flow<CurrentWeather>

    @Query("DELETE FROM currentweather")
    suspend fun deleteAll()

}