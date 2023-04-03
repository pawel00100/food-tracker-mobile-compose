package org.acme.food_tracker_mobile_compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

private val URL = "http://192.168.2.41:8080"

class MenuScreenViewModel : ViewModel() {
    var serverAddressFieldState by mutableStateOf(URL)

    var kcalTargetFieldState by mutableStateOf("2000")

    fun kcalTarget() = kcalTargetFieldState.toIntOrNull() ?: -1

}