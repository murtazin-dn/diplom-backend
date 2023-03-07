package com.example.network.routing

import com.example.database.model.users.Chats
import com.example.model.Chat
import com.example.network.model.response.ChatListResponse
import com.example.network.model.response.ChatResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.configureChatRouting(){
    routing {
        authenticate("jwt") {
            get("/chat/{userId}"){
                val principal = call.principal<JWTPrincipal>()
                val fromUserId = principal?.getClaim("userId", Long::class)

                if (fromUserId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@get
                }

                val paramToUserId = call.parameters["userId"]
                if (paramToUserId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param userId is absent")
                    return@get
                }
                val toUserId: Long
                try {
                    toUserId = paramToUserId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@get
                }

                val chat = Chats.getChatByFirstAndSecondUserId(fromUserId, toUserId)
                if(chat == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                call.respond(HttpStatusCode.OK, ChatResponse(
                    id = chat.id,
                    firstUserId = chat.firstUserId,
                    secondUserId = chat.secondUserId
                ))
            }

            post("/chat/{userId}"){
                val principal = call.principal<JWTPrincipal>()
                val fromUserId = principal?.getClaim("userId", Long::class)

                if (fromUserId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@post
                }

                val paramToUserId = call.parameters["userId"]
                if (paramToUserId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param userId is absent")
                    return@post
                }
                val toUserId: Long
                try {
                    toUserId = paramToUserId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param userId")
                    return@post
                }

                val chat = Chats.getChatByFirstAndSecondUserId(fromUserId, toUserId)
                if(chat != null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val result = Chats.createChat(
                    Chat(
                        id = 0,
                        firstUserId = fromUserId,
                        secondUserId = toUserId
                    )
                )
                if (result == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                call.respond(HttpStatusCode.OK, ChatResponse(
                    id = result.id,
                    firstUserId = result.firstUserId,
                    secondUserId = result.secondUserId
                ))
            }

            post("/chat"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@post
                }

                val list = Chats.getChatListByUserWithUserInfo(userId)


                call.respond(
                    HttpStatusCode.OK, ChatListResponse(
                        list = list
                    )
                )
            }
        }
    }
}