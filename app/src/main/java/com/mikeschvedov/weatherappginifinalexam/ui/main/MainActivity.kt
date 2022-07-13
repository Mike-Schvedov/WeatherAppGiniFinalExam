package com.mikeschvedov.weatherappginifinalexam.ui.main


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.NavController
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.mikeschvedov.weatherappginifinalexam.BuildConfig
import com.mikeschvedov.weatherappginifinalexam.R
import com.mikeschvedov.weatherappginifinalexam.data.mediator.TAG
import com.mikeschvedov.weatherappginifinalexam.databinding.ActivityMainBinding
import com.mikeschvedov.weatherappginifinalexam.components.recievers.NetworkStateReceiver
import com.mikeschvedov.weatherappginifinalexam.components.services.LocationService
import com.mikeschvedov.weatherappginifinalexam.components.workers.LocationCacheWorker
import com.mikeschvedov.weatherappginifinalexam.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val LOCATION_PERMISSIONS_REQUEST_CODE = 34

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener,
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var mainActivityViewModel: MainViewModel

    private var networkStateReceiver: NetworkStateReceiver? = null
    private var snackbar: Snackbar? = null

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var navController: NavController

    private var locationServiceBound = false
    private var locationService: LocationService? = null

    private lateinit var foregroundOnlyBroadcastReceiver: LocationBroadcastReceiver
    private lateinit var sharedPreferences: SharedPreferences

    // Monitors connection to the while-in-use service.
    private val locationServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* View Model */
        mainActivityViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        /* Binding */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Navigation */
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        snackbar = Snackbar.make(
            findViewById(R.id.main_layout),
            R.string.no_internet,
            Snackbar.LENGTH_INDEFINITE
        )

        setNetworkStateReceiver()
        foregroundOnlyBroadcastReceiver = LocationBroadcastReceiver()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        startService(Intent(applicationContext, LocationService::class.java))
        if (foregroundPermissionApproved()) {
            locationService?.subscribeToLocationUpdates()
        } else {
            requestForegroundPermissions()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    //function that sets the network state receiver to the activity
    private fun setNetworkStateReceiver() {
        networkStateReceiver = NetworkStateReceiver(this)
        networkStateReceiver!!.addListener(this)
        applicationContext.registerReceiver(
            networkStateReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onNetworkAvailable() {
        snackbar!!.dismiss()
    }

    override fun onNetworkUnavailable() {
        snackbar!!.show()
    }

    override fun onStart() {
        super.onStart()

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (locationServiceBound) {
            unbindService(locationServiceConnection)
            locationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                findViewById(R.id.main_layout),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Logger.i(TAG, "onRequestPermissionResult")

        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Logger.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                    locationService?.subscribeToLocationUpdates()
                else -> {
                    // Permission denied.
                    // updateButtonState(false)

                    Snackbar.make(
                        findViewById(R.id.main_layout),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    private fun launchPeriodicDatabaseUpdate() {
        // Creating a constraint
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        // Creating a request
        val request = PeriodicWorkRequestBuilder<LocationCacheWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraint)
            .build()
        // Placing the request into a queue
        WorkManager.getInstance(this).enqueue(request)
    }


    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                LocationService.EXTRA_LOCATION
            )

            if (location != null) {
                mainActivityViewModel.updateCatch(location.latitude, location.longitude)
            }
        }
    }
}