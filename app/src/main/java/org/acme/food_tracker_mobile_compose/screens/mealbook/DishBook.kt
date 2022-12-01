package org.acme.food_tracker_mobile_compose.screens.mealbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
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
            SearchRow(viewModel)

            Spacer(modifier = Modifier.height(8.dp))

            for (dish in viewModel.getFilteredDishes()) {
                DishEntry(dish, navController)
            }
        }
    }
}

@Composable
private fun SearchRow(viewModel: DishViewModel) {
    Row {
        OutlinedTextField(
            value = viewModel.searchFieldState,
            label = { Text("Search") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (viewModel.searchFieldState.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable { viewModel.searchFieldState = "" }
                    )
                }
            },
            onValueChange = { viewModel.searchFieldState = it },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
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
