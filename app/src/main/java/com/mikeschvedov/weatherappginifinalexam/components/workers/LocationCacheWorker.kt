package com.mikeschvedov.weatherappginifinalexam.components.workers

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediator
import com.mikeschvedov.weatherappginifinalexam.util.Logger
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class LocationCacheWorker  constructor(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @Inject
    lateinit var contentMediator: ContentMediator
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

   lateinit var currentLocation : Location

    override suspend fun doWork(): Result {

        return try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        currentLocation = location
                    }
                }

            contentMediator.updateDatabaseViaApi(currentLocation.latitude, currentLocation.longitude)

            Result.success()
        } catch (e: Exception) {
            Logger.i("WorkerUpdate Error: ", e.message ?: "No Data")
            Result.failure()
        }
    }
}