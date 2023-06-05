package com.example.network.routing

import com.example.controller.ChatController
import com.example.controller.NotificationsController
import com.example.network.model.HttpResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureNotificationsRouting() {

    val controller by inject<NotificationsController>()

    route("/notifications") {
        post("/subscribe") {
            controller.subscribeNotification(call).let { response ->
                when (response) {
                    is HttpResponse.Error -> call.respond(response.code, response.message)
                    is HttpResponse.Success -> call.respond(response.code, response.body)
                }
            }
        }
        post("/unsubscribe") {
            controller.unsubscribeNotification(call).let { response ->
                when (response) {
                    is HttpResponse.Error -> call.respond(response.code, response.message)
                    is HttpResponse.Success -> call.respond(response.code, response.body)
                }
            }
        }
    }

}