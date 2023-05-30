package com.example.network.routing

import com.example.controller.MessagesController
import com.example.database.model.Chats
import com.example.database.model.Messages
import com.example.model.Message
import com.example.network.model.HttpResponse
import com.example.network.model.request.MessageRequest
import com.example.network.model.response.MessageListResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.inject

fun Route.configureMessageRouting() {
    val controller by inject<MessagesController>()
    route("/chat/{chatId}") {
        route("/messages") {
            get {
                controller.getMessages(call).let { response ->
                    when (response) {
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
        route("/read/{messageId}") {
            get {
                controller.readMessage(call).let { response ->
                    when (response) {
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
    }
    webSocket("/ws/chat/{chatId}") {
        controller.connect(call, this, incoming)
    }

}