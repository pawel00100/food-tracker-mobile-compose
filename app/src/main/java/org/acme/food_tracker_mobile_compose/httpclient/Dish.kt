package org.acme.food_tracker_mobile_compose.httpclient

import kotlinx.serialization.Serializable
import java.time.Instant

data class Dish(
    val id: Long?,
    val name: String,
    val kcal: Int,
    val kcalExpression: String?,
    val created: Instant,
    val barcode: Long?,
)

@Serializable
data class DishWeb(
    val id: Long?,
    val name: String,
    val kcal: Int,
    val kcalExpression: String? = null,
    val created: Long,
    val barcode: Long?,
) {
    constructor(dish: Dish) : this(
        dish.id,
        dish.name,
        dish.kcal,
        dish.kcalExpression,
        dish.created.epochSecond,
        dish.barcode
    )

    constructor(id: Long?, name: String, kcal: Int, kcalExpression: String?, date: Instant, barcode: Long?) : this(
        id,
        name,
        kcal,
        kcalExpression,
        date.epochSecond,
        barcode
    )

    fun asDish() = Dish(id, name, kcal, kcalExpression, Instant.ofEpochSecond(created), barcode)
}