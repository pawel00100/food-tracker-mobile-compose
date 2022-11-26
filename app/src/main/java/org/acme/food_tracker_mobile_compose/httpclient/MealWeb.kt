package org.acme.food_tracker_mobile_compose.httpclient

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MealWeb(
    val id: Long?,
    val name: String,
    val kcal: Int,
    val kcalExpression: String? = null,
    val date: Long,
    val exercise: Boolean,
) {
    constructor(meal: Meal) : this(meal.id, meal.name, meal.kcal, meal.kcalExpression, meal.date.epochSecond, meal.exercise)
    constructor(id: Long?, name: String, kcal: Int, kcalExpression: String?, date: Instant, exercise: Boolean) : this(id, name, kcal, kcalExpression, date.epochSecond, exercise)

    fun asMeal() = Meal(id, name, kcal, kcalExpression, Instant.ofEpochSecond(date), exercise)
}