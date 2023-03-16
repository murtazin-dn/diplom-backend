package com.example.network.routing

import com.example.controller.ChatController
import com.example.database.model.Chats
import com.example.model.Chat
import com.example.network.model.response.ChatListResponse
import com.example.network.model.response.ChatResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureChatRouting() {

    val controller by inject<ChatController>()

    authenticate("jwt") {
        route("/chats"){
            get("/chat/{chatId}"){
                val response = controller.getChatByChatId(call)
                call.respond(response.code, response.body)
            }
            get("/user/{userId}"){
                val response = controller.getChatByUserId(call)
                call.respond(response.code, response.body)
            }
            get(){
                val response = controller.getChats(call)
                call.respond(response.code, response.body)
            }
        }
    }
}