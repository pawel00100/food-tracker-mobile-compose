package org.acme.food_tracker_mobile_compose.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.serialization.Serializable
import net.objecthunter.exp4j.ExpressionBuilder
import org.acme.food_tracker_mobile_compose.httpclient.KtorClient
import org.acme.food_tracker_mobile_compose.httpclient.Meal
import org.acme.food_tracker_mobile_compose.httpclient.MealWeb
import org.acme.food_tracker_mobile_compose.util.Resource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

open class MainScreenViewModel(
    val menuViewModel: MenuScreenViewModel,
    sliderPosition: Float = 0F,
    nameTextFieldState: String = "",
    kcalTextFieldState: String = "",
    exerciseSwitchState: Boolean = false,
) : ViewModel() {
    val client = KtorClient()

    var sliderPosition by mutableStateOf(sliderPosition)
    var nameTextFieldState by mutableStateOf(nameTextFieldState)
    var kcalTextFieldState by mutableStateOf(kcalTextFieldState)
    var exerciseSwitchState by mutableStateOf(exerciseSwitchState)

    val startingPage = 500_000L

    @OptIn(ExperimentalPagerApi::class)
    val datePagerState = PagerState(startingPage.toInt())

    var mealList = mutableStateListOf<Meal>()

    fun validated() = kcalValidated() && nameTextFieldState.isNotEmpty()

    private fun kcalValidated(): Boolean {
        if (kcalTextFieldState.toIntOrNull() != null && kcalTextFieldState.toInt() >= 0) {
            return true
        }

        val expressionResult = evaluateKcal()
        if (expressionResult != null && expressionResult >= 0) {
            return true
        }

        return false
    }

    fun kcalFieldHasPlainNumber() = kcalTextFieldState.toIntOrNull() != null

    fun evaluateKcal(): Int? {
        return try {
            ExpressionBuilder(kcalTextFieldState).build().evaluate().toInt()
        } catch (e: Exception) {
            return null
        }
    }

    fun kcalFieldContainsPotentialExpression() = kcalTextFieldState.isNotEmpty() && !kcalFieldHasPlainNumber()

    open suspend fun submitMeal() = postMeal()

    @OptIn(ExperimentalPagerApi::class)
    suspend fun postMeal(): Boolean {
        val meal = Meal(null, nameTextFieldState, kcalTextFieldState.toInt(), Instant.now().plus(datePagerState.currentPage - startingPage, ChronoUnit.DAYS), exerciseSwitchState)
        val mealPostResponse: Resource<MealPostResponse?> =
            client.postAndReturnBody(MealWeb(null, meal.name, meal.kcal, meal.date, meal.exercise), menuViewModel.serverAddressFieldState + "/meal")


        if (mealPostResponse is Resource.Success && mealPostResponse.result?.id != null) {
            mealList.add(Meal(mealPostResponse.result.id, meal.name, meal.kcal, meal.date, meal.exercise))
            return true
        }

        return false
    }

    suspend fun deleteMeal(id: Long): Boolean {
        if (id !in mealList.map { it.id }) {
            return false
        }

        val succeeded = client.delete(menuViewModel.serverAddressFieldState + "/meal/id/$id")

        return if (succeeded) {
            mealList.removeIf { it.id == id }
        } else {
            false
        }
    }

    suspend fun fetchMeals(): Resource<Unit> {
        val result = client.get<List<MealWeb>>(menuViewModel.serverAddressFieldState + "/meal")
        return when (result) {
            is Resource.Success -> {
                mealList.clear()
                mealList.addAll(result.result.map { it.asMeal() })
                Resource.Success
            }
            is Resource.Failure -> Resource.Failure(result.message)
        }
    }
//    suspend fun fetchMeals(): Result<Unit> {
//        val result = client.get<List<MealWeb>>(menuViewModel.serverAddressFieldState + "/meal")
//        if (result is Resource.Success) {
//            mealList.clear()
//            mealList.addAll(result.result.map { it.asMeal() })
//            return Resource.Success
//        }
//    }

    fun getMeals(day: LocalDate) = mealList.filter { LocalDateTime.ofInstant(it.date, ZoneId.systemDefault()).toLocalDate() == day }.sortedBy { it.date }
    fun getKcalsByDay() =
        mealList.groupBy { LocalDateTime.ofInstant(it.date, ZoneId.systemDefault()).toLocalDate() }.mapValues { it.value.sumOf { it.kcal } }.toSortedMap() //for history view

    fun getMeal(id: Long) = mealList.find { (it.id ?: -1) == id }

    fun kcalSum(day: LocalDate) = mealList.asSequence()
        .filter { LocalDateTime.ofInstant(it.date, ZoneId.systemDefault()).toLocalDate() == day }
        .sumOf { if (it.exercise) it.kcal * -1 else it.kcal }

    fun kcalTarget() = menuViewModel.kcalTarget()

    private fun timeOfDayNumber(): Int = (sliderPosition * 3).roundToInt()

}

@Serializable
data class MealPostResponse(val id: Long?)
