package org.acme.food_tracker_mobile_compose

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.history.HistoryScreen
import org.acme.food_tracker_mobile_compose.screens.mealdetail.MealDetailScreen
import org.acme.food_tracker_mobile_compose.screens.mealdetail.MealEditScreen
import org.acme.food_tracker_mobile_compose.screens.meals.MainScreen
import org.acme.food_tracker_mobile_compose.screens.menu.MenuScreen
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel
import org.acme.food_tracker_mobile_compose.viewmodel.MenuScreenViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val menuViewModel = MenuScreenViewModel()
    val mainScreenViewModel = MainScreenViewModel(menuViewModel)
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        bottomBar = {
            NavBar(
                items = listOf(
                    NavBarItem("Home", Screen.MainScreen.route, Icons.Outlined.Home),
                    NavBarItem("History", Screen.HistoryScreen.route, Icons.Outlined.List),
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(
                        it.route,
                        NavOptions.Builder().setPopUpTo(Screen.MainScreen.route, false).build()
                    )
                }
            )
        }
    ) { padding ->
        NavHost(navController, startDestination = Screen.MainScreen.route) {
            composable(Screen.MainScreen.route) {
                MainScreen(navController = navController, viewModel = mainScreenViewModel, scaffoldState = scaffoldState, padding = padding)
            }
            composable(Screen.MenuScreen.route) {
                MenuScreen(navController = navController, viewModel = menuViewModel, padding = padding)
            }
            composable(
                route = Screen.MealDetailScreen.route + "/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                        nullable = false
                    }
                )
            ) { entry ->
                MealDetailScreen(navController, mainScreenViewModel, padding, entry.arguments?.getLong("id"))
            }
            composable(
                route = Screen.MealEditScreen.route + "/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                        nullable = false
                    }
                )
            ) { entry ->
                MealEditScreen(navController, mainScreenViewModel, scaffoldState, padding, entry.arguments?.getLong("id"))
            }
            composable(Screen.HistoryScreen.route) {
                HistoryScreen(viewModel = mainScreenViewModel, padding = padding)
            }
        }
    }
}

@Composable
fun NavBar(
    items: List<NavBarItem>,
    navController: NavController,
    onItemClick: (NavBarItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation() {
        items.forEach {
            BottomNavigationItem(
                selected = it.route == backStackEntry.value?.destination?.route,
                onClick = { onItemClick(it) },
                icon = { Icon(imageVector = it.icon, contentDescription = it.name) },
                modifier = Modifier.background(MaterialTheme.colors.background),
            )
        }
    }
}

data class NavBarItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)