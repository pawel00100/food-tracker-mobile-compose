package org.acme.food_tracker_mobile_compose.viewmodel

import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.httpclient.DishWeb
import java.time.Instant

class DishEditScreenViewModel(
    menuViewModel: MenuScreenViewModel,
    dishViewModel: DishViewModel,
    val id: Long,
    nameTextFieldState: String = "",
    kcalTextFieldState: String = "",
) : DishCreateViewModel(menuViewModel, dishViewModel, nameTextFieldState, kcalTextFieldState) {

    override suspend fun submitDish() = putDish()

    suspend fun putDish(): Boolean {
        val dish = Dish(
            id,
            nameTextFieldState,
            kcalTextFieldState.toInt(),
            kcalTextFieldState,
            Instant.now(),
        )
        val result = client.put(DishWeb(dish), menuViewModel.serverAddressFieldState + "/dish")

        if (result.isFailure()) {
            return false
        }

        dishViewModel.dishList.removeIf { it.id == id }
        dishViewModel.dishList.add(dish)
        return true
    }

}
