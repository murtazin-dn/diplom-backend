package com.example.network.routing

import com.example.controller.UserInfoController
import com.example.database.model.Users
import com.example.network.model.response.UserInfoResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureUserInfoRouting() {

    val controller by inject<UserInfoController>()

    authenticate("jwt") {
        route("/users/info"){
            get{
                val response = controller.getMyUserInfo(call)
                call.respond(response.code, response.body)
            }
        }
        route("/users/info/{userId}"){
            get{
                val response = controller.getUserInfo(call)
                call.respond(response.code, response.body)
            }
        }
    }
}