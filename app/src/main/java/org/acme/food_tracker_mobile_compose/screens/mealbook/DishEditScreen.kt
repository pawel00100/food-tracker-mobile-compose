package org.acme.food_tracker_mobile_compose.screens.mealbook

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.acme.food_tracker_mobile_compose.screens.meals.DishInput
import org.acme.food_tracker_mobile_compose.screens.menu.BackButton
import org.acme.food_tracker_mobile_compose.viewmodel.DishEditScreenViewModel
import org.acme.food_tracker_mobile_compose.viewmodel.DishViewModel
import org.acme.food_tracker_mobile_compose.viewmodel.MenuScreenViewModel


@Composable
fun DishEditScreen(
    navController: NavController,
    menuViewModel: MenuScreenViewModel,
    dishViewModel: DishViewModel,
    scaffoldState: ScaffoldState,
    padding: PaddingValues,
    dishId: Long?,
) {

    if (dishId == null) {
        return
    }

    val dishEditScreenViewModel = DishEditScreenViewModel(menuViewModel, dishViewModel, dishId)

    if (dishEditScreenViewModel.dishViewModel.getDish(dishId) == null) {
        return
    }

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(32.dp))

            DishInput(dishEditScreenViewModel, scope, scaffoldState, false) { navController.popBackStack() }
        }
    }

}
