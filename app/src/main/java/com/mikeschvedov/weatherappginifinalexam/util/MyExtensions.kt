package com.mikeschvedov.weatherappginifinalexam.util

import android.content.Context
import android.location.Location
import android.widget.ImageView
import androidx.core.content.edit
import com.mikeschvedov.weatherappginifinalexam.R
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.DecimalFormat

fun Double.toCelsius(): Double {
    return ((this - 32) * 5) / 9
}

fun ImageView.insertImage(url: String){
    Picasso.get().load(android.net.Uri.parse(url)).into(this)
}

fun Double.toTwoDecimalAndString(): String {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.DOWN
    return df.format(this)
}

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}


internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getBoolean(KEY_FOREGROUND_ENABLED, false)

    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }
}
