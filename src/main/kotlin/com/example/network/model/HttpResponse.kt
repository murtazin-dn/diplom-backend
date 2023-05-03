package com.example.network.model

import io.ktor.http.*

sealed class HttpResponse<out T> {
    data class Success<T>(val body: T, val code: HttpStatusCode) : HttpResponse<T>()
    data class Error(val message: String, val code: HttpStatusCode) : HttpResponse<Nothing>()


    companion object {
        fun <T> ok(response: T) = Success(body = response, code = HttpStatusCode.OK)

        fun notFound(response: String) = Error(message = response, HttpStatusCode.NotFound)

        fun badRequest(response: String) = Error(message = response, HttpStatusCode.BadRequest)

        fun unauth(response: String) = Error(message = response, HttpStatusCode.Unauthorized)
    }
}

