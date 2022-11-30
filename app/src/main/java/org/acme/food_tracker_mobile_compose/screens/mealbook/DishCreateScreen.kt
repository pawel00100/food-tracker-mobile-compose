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
import org.acme.food_tracker_mobile_compose.viewmodel.DishCreateViewModel


@Composable
fun DishCreateScreen(
    navController: NavController,
    outsideViewModel: DishCreateViewModel,
    scaffoldState: ScaffoldState,
    padding: PaddingValues,
    name: String? = null,
    kcalExpression: String? = null,
) {


    val scope = rememberCoroutineScope()
    val insideViewModel = DishCreateViewModel(
        outsideViewModel.menuViewModel,
        outsideViewModel.dishViewModel,
        name ?: "",
        kcalExpression ?: "",
    )

    DishInput(viewModel = outsideViewModel, scope = scope, scaffoldState = scaffoldState)


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(32.dp))

            DishInput(viewModel = insideViewModel, scope = scope, scaffoldState = scaffoldState) { navController.popBackStack() }
        }
    }

}
