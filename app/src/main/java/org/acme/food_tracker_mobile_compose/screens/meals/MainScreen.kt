package org.acme.food_tracker_mobile_compose.screens.meals

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState,
    padding: PaddingValues,
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            val result = viewModel.fetchMeals()
            if (result.isFailure()) {
                scaffoldState.snackbarHostState.showSnackbar("Failed fetching meals")
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            MealList(viewModel, scope, navController)
            Spacer(modifier = Modifier.height(32.dp))
            MealInput(viewModel, scope, scaffoldState)
        }
    }

}