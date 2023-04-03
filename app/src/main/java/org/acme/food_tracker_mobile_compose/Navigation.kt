package org.acme.food_tracker_mobile_compose

import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.dishbook.*
import org.acme.food_tracker_mobile_compose.screens.history.HistoryScreen
import org.acme.food_tracker_mobile_compose.screens.mealdetail.MealDetailScreen
import org.acme.food_tracker_mobile_compose.screens.mealdetail.MealEditScreen
import org.acme.food_tracker_mobile_compose.screens.meals.MainScreen
import org.acme.food_tracker_mobile_compose.screens.menu.MenuScreen
import org.acme.food_tracker_mobile_compose.viewmodel.*

@Composable
fun Navigation() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val menuViewModel = MenuScreenViewModel()
    val dishViewModel = DishViewModel(menuViewModel)
    val mainScreenViewModel = MainScreenViewModel(
        menuViewModel,
        dishViewModel,
        snackbarPrinter = { scope.launch { scaffoldState.snackbarHostState.showSnackbar(it) } })
    val dishCreateViewModel = DishCreateViewModel(menuViewModel, dishViewModel)
    var dishEditScreenViewModel: DishEditScreenViewModel? = null

    SideEffect {
        scope.launch {
            val result = dishViewModel.fetchDishes()
            if (result.isFailure()) {
                scaffoldState.snackbarHostState.showSnackbar("Failed fetching dishes")
            }
        }
        scope.launch {
            val result = mainScreenViewModel.fetchMeals()
            if (result.isFailure()) {
                scaffoldState.snackbarHostState.showSnackbar("Failed fetching meals")
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            NavBar(
                items = listOf(
                    NavBarItem("Home", Screen.MainScreen.route, Icons.Outlined.Home),
                    NavBarItem("History", Screen.HistoryScreen.route, Icons.Outlined.List),
                    NavBarItem(
                        "Meal Book",
                        Screen.MealBook.route,
                        ImageVector.vectorResource(R.drawable.ic_outline_menu_book_24)
                    ),
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
                MainScreen(
                    navController = navController,
                    viewModel = mainScreenViewModel,
                    scaffoldState = scaffoldState,
                    padding = padding
                )
            }
            composable(Screen.MenuScreen.route) {
                MenuScreen(navController = navController, viewModel = menuViewModel, padding = padding)
            }
            composable(
                route = Screen.MealDetailScreen.route + "/{id}",
                arguments = justIdArg()
            ) { entry ->
                MealDetailScreen(navController, mainScreenViewModel, padding, entry.arguments?.getLong("id"))
            }
            composable(
                route = Screen.MealEditScreen.route + "/{id}",
                arguments = justIdArg()
            ) { entry ->
                MealEditScreen(
                    navController,
                    mainScreenViewModel,
                    scaffoldState,
                    padding,
                    entry.arguments?.getLong("id")
                )
            }
            composable(Screen.HistoryScreen.route) {
                HistoryScreen(viewModel = mainScreenViewModel, padding = padding)
            }
            composable(Screen.MealBook.route) {
                DishBook(navController = navController, viewModel = dishViewModel, padding = padding)
            }
            composable(
                route = Screen.DishDetailScreen.route + "/{id}",
                arguments = justIdArg()
            ) { entry ->
                DishDetailScreen(navController, dishViewModel, padding, entry.arguments?.getLong("id"))
            }
            composable(Screen.DishCreateScreen.route) {
                DishCreateScreen(navController, dishCreateViewModel, scaffoldState, padding)
            }
            composable(
                route = Screen.DishCreateScreen.route + "/{name}" + "/{kcalExpression}",
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                        nullable = true
                    },
                    navArgument("kcalExpression") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { entry ->
                DishCreateScreen(
                    navController,
                    dishCreateViewModel,
                    scaffoldState,
                    padding,
                    entry.arguments?.getString("name"),
                    entry.arguments?.getString("kcalExpression")
                )
            }
            composable(
                route = Screen.DishEditScreen.route + "/{id}",
                arguments = justIdArg()
            ) { entry ->
                val id = entry.arguments?.getLong("id")
                if (dishEditScreenViewModel?.dish?.id != id) {
                    dishEditScreenViewModel = createDishEditScreenViewModel(
                        menuViewModel,
                        dishViewModel,
                        entry.arguments?.getLong("id"),
                        barcode = null
                    )
                }
                if (dishEditScreenViewModel != null) {
                    DishEditScreen(navController, dishEditScreenViewModel!!, scaffoldState, padding)
                }
            }

            composable(
                Screen.DishBarcodeScanner.route + "/{type}",
                arguments = singleArg("type", NavType.StringType)
            ) { entry ->
                val type = entry.arguments?.getString("type")
                val onCapturedBarcode: (Long) -> Unit = when (type) {
                    "find" -> { b -> mainScreenViewModel.searchForBarcode(b) }
                    "edit" -> { b -> dishEditScreenViewModel?.barcode = b }
                    "create" -> { b -> dishCreateViewModel?.barcode = b }
                    else -> { _ -> }
                }
                DishBarcodeScanner(navController, padding, onCapturedBarcode = onCapturedBarcode)
            }
        }
    }
}

@Composable
private fun createDishEditScreenViewModel(
    menuViewModel: MenuScreenViewModel,
    dishViewModel: DishViewModel,
    dishId: Long?,
    barcode: Long?,
): DishEditScreenViewModel? {
    dishId ?: return null
    val dish = dishViewModel.getDish(dishId) ?: return null

    return DishEditScreenViewModel(
        menuViewModel,
        dishViewModel,
        dish,
        dish.name,
        dish.kcalExpression ?: dish.kcal.toString(),
        barcode,
    )

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

private fun <T> singleArg(arg: String, type1: NavType<T>) = listOf(
    navArgument(arg) {
        type = type1
        nullable = false
    }
)

private fun justIdArg() = listOf(
    idArg()
)

private fun idArg() = navArgument("id") {
    type = NavType.LongType
    nullable = false
}