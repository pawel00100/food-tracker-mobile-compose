package org.acme.food_tracker_mobile_compose.screens.mealdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.menu.BackButton
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//TODO: update meal kcal after editk
@Composable
fun MealDetailScreen(
    navController: NavController,
    viewModel: MainScreenViewModel,
    padding: PaddingValues,
    mealId: Long?,
) {
    val scope = rememberCoroutineScope()

    if (mealId == null) {
        return
    }
    val maybeMeal = viewModel.getMeal(mealId)
    if (viewModel.getMeal(mealId) == null) {
        return
    }
    val meal = maybeMeal!!

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = LocalDateTime.ofInstant(meal.date, ZoneId.of("Z")).format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm")),
                    fontSize = 20.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = meal.name,
                    fontSize = 36.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${meal.kcal} kcal",
                    fontSize = 28.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate(Screen.MealEditScreen.withArgs(mealId.toString())) },
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteMeal(meal.id!!)
                        }
                        navController.popBackStack()
                    },
                ) {
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Switch(checked = meal.exercise, onCheckedChange = {})
        }
    }
}