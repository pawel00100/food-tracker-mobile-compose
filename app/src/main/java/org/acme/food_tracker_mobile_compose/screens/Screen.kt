package org.acme.food_tracker_mobile_compose.screens

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object MenuScreen : Screen("menu_screen")
    object MealDetailScreen : Screen("meal_detail_screen")
    object MealEditScreen : Screen("meal_edit_screen")
    object HistoryScreen : Screen("history_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}
