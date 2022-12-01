package org.acme.food_tracker_mobile_compose.viewmodel

import com.google.accompanist.pager.ExperimentalPagerApi
import org.acme.food_tracker_mobile_compose.httpclient.Meal
import org.acme.food_tracker_mobile_compose.httpclient.MealWeb
import java.time.Instant
import java.time.temporal.ChronoUnit

class MealEditScreenViewModel(
    menuViewModel: MenuScreenViewModel,
    dishViewModelTemp: DishViewModel,
    val id: Long,
    sliderPosition: Float = 0F,
    nameTextFieldState: String = "",
    kcalTextFieldState: String = "",
    exerciseSwitchState: Boolean = false,
) : MainScreenViewModel(menuViewModel, dishViewModelTemp, sliderPosition, nameTextFieldState, kcalTextFieldState, exerciseSwitchState) {

    override suspend fun submitMeal() = putMeal()


    @OptIn(ExperimentalPagerApi::class)
    suspend fun putMeal(): Boolean {
        val meal = Meal(id,
            nameTextFieldState,
            kcalTextFieldState.toInt(),
            kcalTextFieldState,
            Instant.now().plus(datePagerState.currentPage - startingPage, ChronoUnit.DAYS),
            exerciseSwitchState)
        val result = client.put(MealWeb(meal), menuViewModel.serverAddressFieldState + "/meal")

        if (result.isFailure()) {
            return false
        }

        mealList.removeIf { it.id == id }
        mealList.add(meal)
        return true
    }
}
