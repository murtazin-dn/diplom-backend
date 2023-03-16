package com.example.network.routing

import com.example.controller.AuthController
import com.example.network.model.request.SignInRequest
import com.example.network.model.request.SignUpRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureAuthRouting() {

    val controller by inject<AuthController>()

    post("/signup") {
        val request = call.receive(SignUpRequest::class)
        val response = controller.signUp(request)
        call.respond(response.code, response.body)
    }
    post("/signin") {
        val request = call.receive(SignInRequest::class)
        val response = controller.signIn(request)
        call.respond(response.code, response.body)
    }

}