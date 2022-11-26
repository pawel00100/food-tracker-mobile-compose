package org.acme.food_tracker_mobile_compose.screens.mealdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.acme.food_tracker_mobile_compose.screens.meals.MealInput
import org.acme.food_tracker_mobile_compose.screens.menu.BackButton
import org.acme.food_tracker_mobile_compose.viewmodel.MainEditScreenViewModel
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel


@Composable
fun MealEditScreen(
    navController: NavController,
    outsideViewModel: MainScreenViewModel,
    scaffoldState: ScaffoldState,
    padding: PaddingValues,
    mealId: Long?,
) {

    if (mealId == null) {
        return
    }
    val maybeMeal = outsideViewModel.getMeal(mealId)
    if (outsideViewModel.getMeal(mealId) == null) {
        return
    }
    val meal = maybeMeal!!


    val scope = rememberCoroutineScope()
    val insideViewModel = MainEditScreenViewModel(
        outsideViewModel.menuViewModel,
        mealId,
        outsideViewModel.sliderPosition,
        meal.name,
        meal.kcalExpression ?: meal.kcal.toString(),
        meal.exercise,
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(32.dp))

            MealInput(insideViewModel, scope, scaffoldState, false) { navController.popBackStack() }
        }
    }

}
