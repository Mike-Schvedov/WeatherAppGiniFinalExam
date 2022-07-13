package com.mikeschvedov.weatherappginifinalexam.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mikeschvedov.weatherappginifinalexam.R
import com.mikeschvedov.weatherappginifinalexam.databinding.FragmentHomeBinding
import com.mikeschvedov.weatherappginifinalexam.util.Constants
import com.mikeschvedov.weatherappginifinalexam.util.insertImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ---------- View model ---------- //
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        // ---------- Binding ---------- //
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setting Up Menu
        setupMenu()
        // Setting Up collectors
        setCollectors()

        return root
    }

    private fun setCollectors() {
        // All Media List
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.weatherState.collectLatest { weatherData ->
                    binding.apply {
                        val tempString = "${weatherData.currentWeather?.tempCelsius}°C"
                        temperatureTextView.text = tempString
                        locationTextView.text = weatherData.currentWeather?.forLocation.toString()
                        summaryTextView.text = weatherData.currentWeather?.summery.toString()
                        val iconName = weatherData.currentWeather?.icon.toString()
                        weatherIconImageview.insertImage(Constants.WEATHER_ICON_PATH + iconName+".png")
                    }
                }
            }
        }
    }


    private fun setupMenu() {
        binding.homeToolbar.inflateMenu(R.menu.menu_main)
        binding.homeToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.map_fragment -> {
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}