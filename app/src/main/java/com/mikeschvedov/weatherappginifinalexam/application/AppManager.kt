package com.mikeschvedov.weatherappginifinalexam.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppManager: Application() {

    override fun onCreate() {
        super.onCreate()

    }

}