package org.acme.food_tracker_mobile_compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.httpclient.DishWeb
import org.acme.food_tracker_mobile_compose.httpclient.KtorClient
import org.acme.food_tracker_mobile_compose.util.Resource


open class DishViewModel(
    private val menuViewModel: MenuScreenViewModel,
) : ViewModel() {
    val client = KtorClient()

    var dishList = mutableStateListOf<Dish>()
    var searchFieldState: String by mutableStateOf("")

    suspend fun deleteDish(id: Long): Boolean {
        if (id !in dishList.map { it.id }) {
            return false
        }

        val succeeded = client.delete(menuViewModel.serverAddressFieldState + "/dish/id/$id")

        return if (succeeded) {
            dishList.removeIf { it.id == id }
        } else {
            false
        }
    }

    suspend fun fetchDishes(): Resource<Unit> {
        val result = client.get<List<DishWeb>>(menuViewModel.serverAddressFieldState + "/dish")
        return when (result) {
            is Resource.Success -> {
                dishList.clear()
                dishList.addAll(result.result.map { it.asDish() })
                Resource.Success
            }
            is Resource.Failure -> Resource.Failure(result.message)
        }
    }

    fun getFilteredDishes(): List<Dish> {
        return getFilteredDishes(searchFieldState)
    }

    fun getFilteredDishes(filter: String): List<Dish> {
        return dishList.filter { it.name.lowercase().contains(filter.trim().lowercase()) }
    }

    fun getDish(id: Long) = dishList.find { (it.id ?: -1) == id }

    fun getDishByBarcode(id: Long) = dishList.find { (it.barcode ?: -1) == id }

}
