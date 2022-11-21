package org.acme.food_tracker_mobile_compose.httpclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import mu.KotlinLogging
import org.acme.food_tracker_mobile_compose.util.Resource

class KtorClient() {
    val logger = KotlinLogging.logger {}

    val client = HttpClient(CIO) {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
    }

    suspend inline fun <reified T> get(endpoint: String): Resource<T> {
        return try {
            Resource.Success(client.get(endpoint).body())
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    suspend inline fun <reified T : Any, reified U> postAndReturnBody(obj: T, endpoint: String): Resource<U?> {
        return try {
            Resource.Success(tryPost(obj, endpoint))

        } catch (e: Exception) {
            logger.warn("failed posting in endpoint $endpoint", e)
            Resource.Failure(e)
        }
    }

    suspend inline fun <reified T : Any, reified U> tryPost(obj: T, endpoint: String): U {
        return client.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(obj)
        }.body()
    }

    suspend inline fun <reified T : Any> put(obj: T, endpoint: String): Resource<Unit> {
        return try {
            tryPut(obj, endpoint)
            Resource.Success(Unit)
        } catch (e: Exception) {
            logger.warn("failed posting in endpoint $endpoint", e)
            Resource.Failure(e)
        }
    }

    suspend inline fun <reified T : Any> tryPut(obj: T, endpoint: String): Boolean {
        val response = client.put(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(obj)
        }
        return (response.status.value / 100) == 2
    }

    suspend inline fun delete(endpoint: String): Boolean {
        val response = client.delete(endpoint)
        return (response.status.value / 100) == 2
    }
}

