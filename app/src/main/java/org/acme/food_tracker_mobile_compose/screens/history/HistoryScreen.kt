package org.acme.food_tracker_mobile_compose.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.acme.food_tracker_mobile_compose.screens.misccomponents.DailyCalorieIndicator
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    viewModel: MainScreenViewModel,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = 30.dp, vertical = 30.dp)
            .fillMaxSize()
    ) {
        for ((day, kcal) in viewModel.getKcalsByDay()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = day.format(DateTimeFormatter.ofPattern("dd.MM.yy")))
                DailyCalorieIndicator(goal = viewModel.kcalTarget(), kcal = kcal)
            }
        }
    }
}