package com.mikeschvedov.weatherappginifinalexam.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class CurrentWeather(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val summery: String,
    val tempCelsius: String,
    val forLocation: String?,
    val icon: String
) : Parcelable

