package org.acme.food_tracker_mobile_compose.httpclient

import java.time.Instant

data class Meal(
    val id: Long?,
    val name: String,
    val kcal: Int,
    val date: Instant,
    val exercise: Boolean,
)