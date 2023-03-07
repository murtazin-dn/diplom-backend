package com.example.network.routing

import com.example.database.model.users.Chats
import com.example.database.model.users.Messages
import com.example.model.Message
import com.example.network.model.request.MessageRequest
import com.example.network.model.request.RegisterRequest
import com.example.network.model.response.MessageListResponse
import com.example.network.model.response.MessageResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureMessageRouting(){
    routing {
        authenticate("jwt") {
            post("chat/{chatId}/message") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@post
                }

                val request = call.receive(MessageRequest::class)


                val paramChatId = call.parameters["chatId"]
                if (paramChatId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param userId is absent")
                    return@post
                }
                val chatId: Long
                try {
                    chatId = paramChatId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@post
                }

                val chat = Chats.getChatById(chatId)
                if(chat == null){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@post
                }

                val message = Messages.createMessage(
                    Message(
                        id = 0,
                        chatId = chatId,
                        userId = userId,
                        text = request.text,
                        date = System.currentTimeMillis()
                    )
                )
            }
            get("chat/{chatId}/message") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@get
                }


                val paramChatId = call.parameters["chatId"]
                if (paramChatId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param userId is absent")
                    return@get
                }
                val chatId: Long
                try {
                    chatId = paramChatId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@get
                }

                val chat = Chats.getChatById(chatId)
                if(chat == null){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@get
                }

                val messages = Messages.getMessagesByChatId(chatId)

                call.respond(HttpStatusCode.OK, MessageListResponse(
                    list = messages
                ))
            }
        }
    }
}