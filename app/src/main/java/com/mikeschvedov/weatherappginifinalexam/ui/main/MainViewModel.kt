package com.mikeschvedov.weatherappginifinalexam.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikeschvedov.weatherappginifinalexam.data.mediator.ContentMediator

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contentMediator: ContentMediator
) : ViewModel() {


    fun updateCatch(lan : Double, lng: Double){
        viewModelScope.launch {
            contentMediator.updateDatabaseViaApi(lan, lng)
        }
    }



}