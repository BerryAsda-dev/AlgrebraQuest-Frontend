package com.example.algrebraquest.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class RegisterRequest(val username: String, val displayName: String, val password: String)

/**
 * Registro de nuevos ALUMNOS (POST /auth/register).
 * Vive en su propio archivo para no modificar [ApiClient]: usa la misma constante [RAILWAY_BASE_URL].
 * El backend fuerza el rol ALUMNO; el cliente nunca envía un rol.
 */
class RegisterApi {
    private val http = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; encodeDefaults = true }) }
        install(HttpTimeout) { requestTimeoutMillis = 20_000; connectTimeoutMillis = 8_000 }
    }

    suspend fun register(username: String, displayName: String, password: String): LoginResponse {
        val response = try {
            http.post("$RAILWAY_BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, displayName, password))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw ApiFailure(0, "No se pudo conectar con el servidor. Revisa tu conexión a internet e inténtalo de nuevo.")
        }
        if (!response.status.isSuccess()) {
            val message = runCatching { response.body<ApiError>().message }.getOrNull()
                ?: "Error del servidor (${response.status.value})"
            throw ApiFailure(response.status.value, message)
        }
        return response.body()
    }

    fun close() = http.close()
}
