package org.acme.food_tracker_mobile_compose.screens

import androidx.navigation.NavController

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object MenuScreen : Screen("menu_screen")
    object MealDetailScreen : Screen("meal_detail_screen")
    object MealEditScreen : Screen("meal_edit_screen")
    object HistoryScreen : Screen("history_screen")
    object MealBook : Screen("meal_book")
    object DishDetailScreen : Screen("dish_detail_screen")
    object DishCreateScreen : Screen("dish_create_screen")
    object DishEditScreen : Screen("dish_edit_screen")
    object DishBarcodeScanner : Screen("dish_barcode_scanner_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}

fun NavController.navigate(screen: Screen) {
    navigate(screen.route)
}