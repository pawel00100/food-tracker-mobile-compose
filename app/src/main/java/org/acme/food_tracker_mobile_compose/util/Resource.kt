package org.acme.food_tracker_mobile_compose.util

sealed class Resource<T> {
    companion object {
        val Success = Success(Unit)
    }

    data class Success<T>(public val result: T) : Resource<T>() {
    }

    data class Failure<T>(val message: String?) : Resource<T>() {
        override fun toString() = message ?: ""

        constructor(throwable: Throwable) : this(throwable.message)
    }

    fun orElse(alternative: T): T {
        return when (this) {
            is Success -> result
            is Failure -> alternative
        }
    }

    fun isSuccessful(): Boolean {
        return this is Success
    }

    fun isFailure(): Boolean {
        return this is Failure
    }

    fun getOrNull(): T? {
        if (this is Success) {
            return result
        }
        return null
    }
}
