package com.example.network.model

import io.ktor.http.*

sealed class HttpResponse<T> {
    abstract val body: T
    abstract val code: HttpStatusCode

    data class Ok<T>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.OK
    }

    data class NotFound<T>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.NotFound
    }

    data class BadRequest<T>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.BadRequest
    }

    data class Unauthorized<T>(override val body: T) : HttpResponse<T>() {
        override val code: HttpStatusCode = HttpStatusCode.Unauthorized
    }

    companion object {
        fun <T> ok(response: T) = Ok(body = response)

        fun <T> notFound(response: T) = NotFound(body = response)

        fun <T> badRequest(response: T) = BadRequest(body = response)

        fun <T> unauth(response: T) = Unauthorized(body = response)
    }
}

