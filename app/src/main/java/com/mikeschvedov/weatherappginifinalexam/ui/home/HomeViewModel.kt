package com.mikeschvedov.weatherappginifinalexam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediator
import com.mikeschvedov.weatherappginifinalexam.util.NullableWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) : ViewModel() {

    private var _weatherState = MutableStateFlow(NullableWrapper())
    val weatherState = _weatherState.asStateFlow()

    init {
        viewModelScope.launch {
            contentMediator.getWeatherFromDatabase()
                .flowOn(Dispatchers.IO)
                .collect { weather ->
                    _weatherState.value = NullableWrapper(weather)
                }
        }
    }

}