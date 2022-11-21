package org.acme.food_tracker_mobile_compose.screens.misccomponents

import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DailyCalorieIndicator(goal: Int, kcal: Int, modifier: Modifier = Modifier) {
    val goalExceeded = goal > kcal
    val ratio = if (goalExceeded) kcal.toFloat() / goal else goal.toFloat() / kcal
    if (goalExceeded) {
        LinearProgressIndicator(
            ratio,
            modifier = modifier,
        )
    } else {
        LinearProgressIndicator(
            ratio,
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.error,
        )
    }
}