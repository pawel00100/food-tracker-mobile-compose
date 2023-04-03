package org.acme.food_tracker_mobile_compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import net.objecthunter.exp4j.ExpressionBuilder
import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.httpclient.DishWeb
import org.acme.food_tracker_mobile_compose.httpclient.KtorClient
import org.acme.food_tracker_mobile_compose.util.Resource
import java.time.Instant


open class DishCreateViewModel(
    val menuViewModel: MenuScreenViewModel,
    val dishViewModel: DishViewModel,
    nameTextFieldState: String = "",
    kcalTextFieldState: String = "",
    barcode: Long? = null,
) : ViewModel() {
    val client = KtorClient()

    var nameTextFieldState by mutableStateOf(nameTextFieldState)
    var kcalTextFieldState by mutableStateOf(kcalTextFieldState)
    var barcode by mutableStateOf(barcode)

    fun validated() = kcalValidated() && nameTextFieldState.isNotEmpty()

    private fun kcalValidated(): Boolean {
        if (readKcalTextFieldState().toIntOrNull() != null && readKcalTextFieldState().toInt() >= 0) {
            return true
        }

        val expressionResult = evaluateKcal()
        if (expressionResult != null && expressionResult >= 0) {
            return true
        }

        return false
    }

    fun kcalFieldHasPlainNumber() = readKcalTextFieldState().toIntOrNull() != null

    fun evaluateKcal(): Int? {
        return try {
            ExpressionBuilder(readKcalTextFieldState()).build().evaluate().toInt()
        } catch (e: Exception) {
            return null
        }
    }

    fun kcalFieldContainsPotentialExpression() =
        kcalTextFieldState.isNotEmpty() && !kcalFieldHasPlainNumber()

    private fun readKcalTextFieldState() = kcalTextFieldState.replace(",", ".").replace("×", "*")

    open suspend fun submitDish() = postDish()

    suspend fun postDish(): Boolean {
        val kcal = evaluateKcal() ?: return false
        val date = Instant.now()
        val dish =
            Dish(null, nameTextFieldState.trim(), kcal, readKcalTextFieldState(), date, barcode)
        val dishPostResponse: Resource<DishPostResponse?> =
            client.postAndReturnBody(
                DishWeb(
                    null,
                    dish.name,
                    dish.kcal,
                    dish.kcalExpression,
                    dish.created,
                    dish.barcode
                ),
                menuViewModel.serverAddressFieldState + "/dish"
            )

        if (dishPostResponse is Resource.Success && dishPostResponse.result?.id != null) {
            dishViewModel.dishList.add(
                Dish(
                    dishPostResponse.result.id,
                    dish.name,
                    dish.kcal,
                    dish.kcalExpression,
                    dish.created,
                    dish.barcode
                )
            )
            return true
        }
        return false
    }
}

@Serializable
data class DishPostResponse(val id: Long?)
