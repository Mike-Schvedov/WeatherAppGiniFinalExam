package com.mikeschvedov.weatherappginifinalexam.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

class NetworkStatusChecker @Inject constructor (private val connectivityManager: ConnectivityManager?) {

    // inline methods are copied to the user class
    inline fun performIfConnectedToInternet(action: () -> Unit) {
        if (hasInternetConnection()) {
            action()
        }
    }

    fun hasInternetConnection(): Boolean {
        // your active network
        val network = connectivityManager?.activeNetwork ?: return false
        // the capabilities of that network
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        //if capabilities is WIFI/CELLULAR/VPN true
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
}