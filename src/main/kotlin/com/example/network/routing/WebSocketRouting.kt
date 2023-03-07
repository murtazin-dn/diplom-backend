package com.example.network.routing

import com.example.database.model.users.Chats
import com.example.database.model.users.Messages
import com.example.model.Message
import com.example.network.messages.controller.MessagesController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Application.configureWebSocketRouting(controller: MessagesController){
    routing {
        authenticate("jwt"){
            webSocket("/ws/chat/{chatId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@webSocket
                }

                val paramChatId = call.parameters["chatId"]
                if (paramChatId == null) {
                    call.respond(HttpStatusCode.Conflict, "Param chatId is absent")
                    return@webSocket
                }
                val chatId: Long
                try {
                    chatId = paramChatId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.Conflict, "Invalid param chatId")
                    return@webSocket
                }

                val chat = Chats.getChatById(chatId)
                if(chat == null){
                    call.respond(HttpStatusCode.Conflict, "Invalid param chatId")
                    return@webSocket
                }

                if(userId != chat.secondUserId && userId != chat.firstUserId){
                    call.respond(HttpStatusCode.Conflict, "Invalid param chatId")
                    return@webSocket
                }
                controller.joinChat(userId, chatId, this)

                try {
                    incoming
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val message = Messages.createMessage(
                            Message(
                                id = 0,
                                chatId = chatId,
                                userId = userId,
                                text = receivedText,
                                date = System.currentTimeMillis()
                            )
                        )
                        controller.sendMessage(message!!)
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    controller.disconnect(userId, chatId)
                }
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}