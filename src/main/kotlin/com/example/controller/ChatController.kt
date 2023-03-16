package com.example.controller

import com.example.database.model.Chats
import com.example.database.model.Users
import com.example.model.Chat
import com.example.network.model.HttpResponse
import com.example.network.model.response.ChatListResponse
import com.example.network.model.response.ChatResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class ChatControllerImpl : ChatController{
    override suspend fun getChats(call: ApplicationCall): HttpResponse<Any> {
        return try{
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val chatList = Chats.getChatListByUserWithUserInfo(userId)
            HttpResponse.ok(ChatListResponse(chatList))
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getChatByUserId(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val firstUserId = principal?.getClaim("userId", Long::class)!!
            val secondUserId = call.parameters["userId"]?.toLongOrNull()
                ?: throw BadRequestException("Invalid param user id")
            val userInfo = Users.getUserInfo(secondUserId)
                ?: throw BadRequestException("User with this id does not exists")
            val chat = Chats.getChatByFirstAndSecondUserId(firstUserId, secondUserId)
                ?: Chats.createChat(
                    Chat(
                        id = 0,
                        firstUserId = firstUserId,
                        secondUserId = secondUserId
                    )
                ) ?: throw BadRequestException("Error create chat")
            HttpResponse.ok(
                ChatResponse(
                    id = chat.id,
                    firstUserId = firstUserId,
                    secondUser = userInfo.toUserInfoResponse()
                )
            )
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getChatByChatId(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val firstUserId = principal?.getClaim("userId", Long::class)!!
            val chatId = call.parameters["chatId"]?.toLongOrNull()
                ?: throw BadRequestException("Invalid param chat id")
            val chat = Chats.getChatById(chatId)
                ?: throw BadRequestException("Chat with this id does not exists")
            val secondUser = when{
                (firstUserId == chat.firstUserId) -> Users.getUserInfo(chat.secondUserId)
                (firstUserId == chat.secondUserId) -> Users.getUserInfo(chat.firstUserId)
                else -> throw BadRequestException("Error")
            } ?: throw BadRequestException("Error")
            HttpResponse.ok(
                ChatResponse(
                    id = chat.id,
                    firstUserId = firstUserId,
                    secondUser = secondUser.toUserInfoResponse()
                )
            )
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }
}

interface ChatController{
    suspend fun getChats(call: ApplicationCall): HttpResponse<Any>
    suspend fun getChatByUserId(call: ApplicationCall): HttpResponse<Any>
    suspend fun getChatByChatId(call: ApplicationCall): HttpResponse<Any>
}