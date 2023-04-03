package org.acme.food_tracker_mobile_compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class PermissionStatus {
    GRANTED, REJECTED, UNKNOWN
}

class DishBarcodeScannerViewModel {
    var cameraPermissionGranted by mutableStateOf(PermissionStatus.UNKNOWN)
}