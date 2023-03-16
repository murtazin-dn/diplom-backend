package com.example.network.routing

import com.example.controller.SubscribersController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureSubscribersRouting() {

    val controller by inject<SubscribersController>()

    authenticate("jwt") {
        route("/subscribers") {
            get("/list"){
                val response = controller.getSubscribers(call)
                call.respond(response.code, response.body)
            }
            post("/{userId}") {
                val response = controller.subscribe(call)
                call.respond(response.code, response.body)
            }
            delete("/{userId}") {
                val response = controller.unsubscribe(call)
                call.respond(response.code, response.body)
            }

        }
    }

}