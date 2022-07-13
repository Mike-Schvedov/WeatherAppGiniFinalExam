package com.mikeschvedov.weatherappginifinalexam.util

import android.util.Log
import com.mikeschvedov.weatherappginifinalexam.BuildConfig

class Logger {
    companion object{
        fun i(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
        fun e(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
    }
}