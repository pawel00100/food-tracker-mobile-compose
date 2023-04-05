package org.acme.food_tracker_mobile_compose.screens.misccomponents

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ClearTextFieldIcon(fieldText: String, clearField: () -> Unit) {
    if (fieldText.isNotEmpty()) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
            modifier = Modifier.clickable { clearField() }
        )
    }
}