package com.example.network.routing

import com.example.controller.AuthController
import com.example.network.model.HttpResponse
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
        controller.signUp(request).let {response ->
            when(response){
                is HttpResponse.Error -> call.respond(response.code, response.message)
                is HttpResponse.Success -> call.respond(response.code, response.body)
            }
        }
    }
    post("/signin") {
        val request = call.receive(SignInRequest::class)
        controller.signIn(request).let {response ->
            when(response){
                is HttpResponse.Error -> call.respond(response.code, response.message)
                is HttpResponse.Success -> call.respond(response.code, response.body)
            }
        }
    }
    get("/email/{email}") {
        controller.findEmail(call).let {response ->
            when(response){
                is HttpResponse.Error -> call.respond(response.code, response.message)
                is HttpResponse.Success -> call.respond(response.code, response.body)
            }
        }
    }

}