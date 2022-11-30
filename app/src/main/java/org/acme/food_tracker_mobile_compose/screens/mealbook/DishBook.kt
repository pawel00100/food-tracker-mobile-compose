package org.acme.food_tracker_mobile_compose.screens.mealbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.navigate
import org.acme.food_tracker_mobile_compose.viewmodel.DishViewModel

@Composable
fun DishBook(
    navController: NavController,
    viewModel: DishViewModel,
    padding: PaddingValues,
) {
    Box(modifier = Modifier
        .padding(padding)
        .fillMaxSize()
    ) {
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd),
            onClick = {
                navController.navigate(Screen.DishCreateScreen)
            },
        ) {
            Icon(Icons.Filled.Add, "Add")
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 30.dp)
                .fillMaxSize()
        ) {
            for (dish in viewModel.dishList) {
                DishEntry(dish, navController)
            }
        }
    }
}

@Composable
private fun DishEntry(
    dish: Dish,
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clickable { navController.navigate(Screen.DishDetailScreen.withArgs(dish.id.toString())) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = dish.name)
        Text(text = "${dish.kcal} kcal")
    }
}
