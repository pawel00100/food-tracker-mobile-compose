package org.acme.food_tracker_mobile_compose.screens.dishbook

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.menu.BackButton
import org.acme.food_tracker_mobile_compose.viewmodel.DishViewModel

//TODO: update dish kcal after edit
@Composable
fun DishDetailScreen(
    navController: NavController,
    viewModel: DishViewModel,
    padding: PaddingValues,
    dishId: Long?,
) {
    val scope = rememberCoroutineScope()

    if (dishId == null) {
        return
    }
    val maybeDish = viewModel.getDish(dishId)
    if (viewModel.getDish(dishId) == null) {
        return
    }
    val dish = maybeDish!!

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
                    text = dish.name,
                    fontSize = 36.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${dish.kcal} kcal",
                    fontSize = 28.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate(Screen.DishEditScreen.withArgs(dishId.toString())) },
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.deleteDish(dish.id!!)
                        }
                        navController.popBackStack()
                    },
                ) {
                    Text("Delete")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}