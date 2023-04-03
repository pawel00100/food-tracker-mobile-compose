package org.acme.food_tracker_mobile_compose.viewmodel

import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.httpclient.DishWeb
import java.time.Instant

class DishEditScreenViewModel(
    menuViewModel: MenuScreenViewModel,
    dishViewModel: DishViewModel,
    val dish: Dish,
    nameTextFieldState: String = "",
    kcalTextFieldState: String = "",
    barcode: Long? = null,
) : DishCreateViewModel(
    menuViewModel,
    dishViewModel,
    nameTextFieldState,
    kcalTextFieldState,
    barcode
) {

    override suspend fun submitDish() = putDish()

    suspend fun putDish(): Boolean {
        val kcal = evaluateKcal() ?: return false

        val newDish = Dish(
            dish.id,
            nameTextFieldState,
            kcal,
            kcalTextFieldState,
            Instant.now(),
            barcode,
        )
        val result = client.put(DishWeb(newDish), menuViewModel.serverAddressFieldState + "/dish")

        if (result.isFailure()) {
            return false
        }

        dishViewModel.dishList.removeIf { it.id == dish.id }
        dishViewModel.dishList.add(newDish)
        return true
    }

}
