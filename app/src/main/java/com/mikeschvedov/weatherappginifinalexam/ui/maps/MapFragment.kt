package com.mikeschvedov.weatherappginifinalexam.ui.maps


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mikeschvedov.weatherappginifinalexam.R
import com.mikeschvedov.weatherappginifinalexam.databinding.FragmentMapBinding
import com.mikeschvedov.weatherappginifinalexam.util.Constants
import com.mikeschvedov.weatherappginifinalexam.util.insertImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    lateinit var mapViewModel: MapViewModel

    var loadingInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ---------- View model ---------- //
        mapViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]
        // ---------- Binding ---------- //
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize map fragment
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)

        // Setting Collectors
        setCollectors()

        // Setting Up onClick listeners
        onClickListeners()

        return root
    }

    private fun setCollectors() {
        // ----- Weather Data Available ---- //
        lifecycleScope.launch {
            mapViewModel.weatherReady.collect { weatherData: Boolean ->
                if (weatherData) {
                    binding.progressBar.visibility = View.GONE
                    binding.infoPanel.visibility = View.VISIBLE
                    loadingInProgress = false
                }
            }
        }
        // ----- Location Name ---- //
        lifecycleScope.launch {
            mapViewModel.locationName.collect { name ->
                binding.weatherLocationTextview.text = name
            }
        }
        // ----- Weather State ---- //
        lifecycleScope.launch {
            mapViewModel.weatherState.collect { weather ->
                binding.weatherIcon.insertImage(Constants.WEATHER_ICON_PATH + weather.icon + ".png")
                val statusString = "${weather.temp}°C, ${weather.summary}"
                binding.weatherStatusTextview.text = statusString
            }
        }
    }

    private fun onClickListeners() {
        binding.backToHomeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latlang ->
            if (mapViewModel.networkStatusChecker.hasInternetConnection()) {// Clear map from previous markers
                mMap.clear()
                // Create new marker
                val newMarker = LatLng(latlang.latitude, latlang.longitude)

                // Wait to get response with data
                binding.progressBar.visibility = View.VISIBLE
                binding.infoPanel.visibility = View.GONE
                loadingInProgress = true

                // display weather data
                mapViewModel.displayLocationInfo(latlang.latitude, latlang.longitude)
                // Show marker on make and it's title/info
                mMap.addMarker(
                    MarkerOptions()
                        .position(newMarker)
                )
                // Move camera to marker location
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newMarker))
            } else {

                showAlertDialog()
            }
        }
    }

    private fun showAlertDialog() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // set message of alert dialog
        dialogBuilder.setMessage(getString(R.string.warning))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.negative_btn), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(getString(R.string.offline))
        // show alert dialog
        alert.show()
    }


}