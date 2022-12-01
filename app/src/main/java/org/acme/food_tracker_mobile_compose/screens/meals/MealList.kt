package org.acme.food_tracker_mobile_compose.screens.meals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.R
import org.acme.food_tracker_mobile_compose.httpclient.Meal
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.misccomponents.DailyCalorieIndicator
import org.acme.food_tracker_mobile_compose.ui.theme.BlankedText
import org.acme.food_tracker_mobile_compose.ui.theme.PaleGreen
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MealList(viewModel: MainScreenViewModel, scope: CoroutineScope, navController: NavController) {

    val dialogState = datePicker(scope, viewModel)

    HorizontalPager(count = 1000_000, state = viewModel.datePagerState) { page ->
        Column() {
            val dayOffset = page - 500_000L
            val day = LocalDateTime.now().toLocalDate().plusDays(dayOffset)

            DateRow(dialogState, day, navController)

            Spacer(modifier = Modifier.height(16.dp))

            Summary(viewModel, day)

            Spacer(modifier = Modifier.height(24.dp))

            val meals = viewModel.getMeals(day)
            for (meal in viewModel.getMeals(day)) {
                MealEntry(meal, navController)
            }
            for (i in meals.size until 6) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun Summary(viewModel: MainScreenViewModel, day: LocalDate) {
    val goal = viewModel.kcalTarget()
    val kcal = viewModel.kcalSum(day)

    DailyCalorieIndicator(goal, kcal, Modifier.fillMaxWidth())

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "Sum: ${viewModel.kcalSum(day)} kcal")
        Text(text = "Goal: $goal kcal")
    }
}


@Composable
private fun DateRow(dialogState: MaterialDialogState, day: LocalDate, navController: NavController) {
    Box() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = day.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                fontSize = 24.sp,
                modifier = Modifier.clickable { dialogState.show() }
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = {
                navController.navigate(Screen.MenuScreen.route)
            }) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "Open date picker")
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun datePicker(
    scope: CoroutineScope,
    viewModel: MainScreenViewModel
): MaterialDialogState {
    val dialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            scope.launch {
                val delta = date.toEpochDay() - LocalDateTime.now().toLocalDate().toEpochDay()
                viewModel.datePagerState.scrollToPage(500_000 + delta.toInt())
            }
        }
    }
    return dialogState
}

//@Preview
@Composable
private fun MealEntry(
    meal: Meal = Meal(10, "name", 500, "500", Instant.now(), true),
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clickable { navController.navigate(Screen.MealDetailScreen.withArgs(meal.id.toString())) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = meal.date.atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
            color = MaterialTheme.colors.BlankedText,
        )
        Spacer(modifier = Modifier.width(24.dp))

        Box(Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            Text(
                text = meal.name,
                softWrap = false,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(Modifier
            .width(72.dp)
        ) {
            Text(text = "${meal.kcal} kcal")
        }

        Box(Modifier.width(24.dp)) {
            if (meal.exercise) {
                Icon(painterResource(R.drawable.ic_baseline_directions_bike_24), contentDescription = "Is an exercise", tint = PaleGreen)
            }
        }
    }
}
